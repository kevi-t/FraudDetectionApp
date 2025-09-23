package fraud.detection.app.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.UserDetailsService;
import fraud.detection.app.utils.JwtTokenUtil;
import fraud.detection.app.utils.LogFileCreator;
import fraud.detection.app.utils.SessionMsg;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final LogFileCreator logFileCreator;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final SessionMsg msg = new SessionMsg();

    // Define whitelisted paths that skip JWT check
    private static final Set<String> WHITELIST = Set.of(
            "/fraud/app/login",
            "/fraud/app/register",
            "/otp/send",
            "/otp/verify",
            "/stk-deposit-request",
            "/otp/forgot/password/reset",
            "/fraud/app/sendmoney",
            "/otp/sendregisterotp"
    );

    @Autowired
    public JwtTokenFilter(LogFileCreator logFileCreator,
                          UserDetailsService userDetailsService,
                          JwtTokenUtil jwtTokenUtil) {
        this.logFileCreator = logFileCreator;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // Skip JWT check for whitelisted paths
        if (WHITELIST.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                token = authorizationHeader.substring(7).trim(); // fix parsing
                username = jwtTokenUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtTokenUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());

                        authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (Exception e) {
                log.warn("JWT validation failed: {}", e.getMessage());
                UniversalResponse errorResponse = msg.getSessionErrorMsg();
                String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);

                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // send 401
                response.getWriter().write(jsonResponse);
                return; // stop filter chain on invalid token
            }
        }

        // Always continue if no error
        filterChain.doFilter(request, response);
    }
}
