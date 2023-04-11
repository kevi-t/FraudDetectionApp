package fraud.detection.app.configurations;

import fraud.detection.app.filter.jwtTokenFilter;
import fraud.detection.app.services.UserDetailsService;
import fraud.detection.app.utils.JwtTokenUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Transactional
@org.springframework.transaction.annotation.Transactional
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfigaration {
    private UserDetailsService userDetailsService;
    jwtTokenFilter JwtTokenfileter;
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoderD());
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests().requestMatchers
                        ("/fraud/app/register","/fraud/app/login","/otp/send","/otp/verify").permitAll()
                .and()
                .authorizeHttpRequests().requestMatchers
                        ("/api/auth//getusers").hasAuthority("Admin")
                .anyRequest().authenticated();
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(JwtTokenfileter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean //spring bean
    public PasswordEncoder passwordEncoderD() {
        return new BCryptPasswordEncoder(); //return if converted to shaa
    }

}