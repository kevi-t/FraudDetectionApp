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

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final LogFileCreator logFileCreator;
    private final UserDetailsService UserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private SessionMsg msg = new SessionMsg();

    @Autowired
    public JwtTokenFilter(LogFileCreator logFileCreator
            , fraud.detection.app.services.UserDetailsService userDetailsService
            , JwtTokenUtil jwtTokenUtil) {
        this.logFileCreator = logFileCreator;
        UserDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().equals("/fraud/app/login")) {
            filterChain.doFilter(request, response);
        }
        else if (request.getServletPath().equals("/fraud/app/register")) {
            filterChain.doFilter(request, response);

        }
        else if (request.getServletPath().equals("/otp/send")) {
            filterChain.doFilter(request, response);
        }
        else if (request.getServletPath().equals("/otp/verify")){
            filterChain.doFilter(request, response);
        }
        else if (request.getServletPath().equals("/stk-deposit-request")){
            filterChain.doFilter(request, response);
        }
        else if (request.getServletPath().equals("/fraud/app/withdraw")){
            filterChain.doFilter(request, response);
        }
        else {

            String authorizationHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {

                try {
                    token = authorizationHeader.substring(8);
                    username = jwtTokenUtil.extractUsername(token);

                    if(username != null && SecurityContextHolder.getContext().getAuthentication()==null){
                        UserDetails userDetails = UserDetailsService.loadUserByUsername(username);

                        if (jwtTokenUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                            filterChain.doFilter(request, response);
                        }
                    }
                }
                catch (Exception e) {
                    log.info("exceptions " + e.getMessage());
                    UniversalResponse errorResponse = msg.getSessionErrorMsg();
                    String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse); // Convert UniversalResponse to JSON
                    response.setContentType("application/json;charset=UTF-8"); // Set response content type to JSON
                    response.setStatus(HttpServletResponse.SC_OK); // Set response status to 200 OK
                    response.getWriter().write(jsonResponse); // Write JSON response to the response writer
                }
            }
            else {
                filterChain.doFilter(request, response);
            }

        }
    }
}