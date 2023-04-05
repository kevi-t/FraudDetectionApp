package fraud.detection.app.configurations;

import fraud.detection.app.filter.JwtTokenFilter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Transactional
@org.springframework.transaction.annotation.Transactional
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfigaration {

    private final JwtTokenFilter jwtTokenFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests().requestMatchers("/fraud/app/**")
                       .permitAll()
                .and()
                .authorizeHttpRequests().requestMatchers
                        ("/api/auth//getusers").hasAuthority("Admin")
                .anyRequest().authenticated();
        http.authenticationProvider(authenticationProvider).addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}