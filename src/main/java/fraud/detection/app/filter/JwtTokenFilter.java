package fraud.detection.app.filter;

import fraud.detection.app.services.UserDetailsService;
import fraud.detection.app.utils.JwtTokenUtil;
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
public class JwtTokenFilter extends OncePerRequestFilter  {

    private final UserDetailsService UserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtTokenFilter(fraud.detection.app.services.UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        UserDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().equals("/fraud/app/login")) {
            filterChain.doFilter(request, response);
        }
        else if (request.getServletPath().equals("/fraud/app/register")) {
            filterChain.doFilter(request, response);

        } else if (request.getServletPath().equals("/otp/send")) {
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
           // UniversalResponse universalResponse;
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
//                    universalResponse = UniversalResponse.builder().status(401).message("Token has expired to perform this request").build();
//                    response.setContentType(APPLICATION_JSON_VALUE);
//                    new ObjectMapper().writeValue(response.getOutputStream(), universalResponse);
                }
            }
            else {
                filterChain.doFilter(request, response);
            }

        }
    }
}