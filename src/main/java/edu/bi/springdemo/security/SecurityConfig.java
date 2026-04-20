package edu.bi.springdemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;

    public SecurityConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/error").permitAll()

                        // users
                        .requestMatchers(HttpMethod.POST, "/users/**").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/users/**").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("LIBRARIAN")

                        // books
                        .requestMatchers(HttpMethod.GET, "/books/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/books/**").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/books/**").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole("LIBRARIAN")

                        // loans
                        .requestMatchers(HttpMethod.GET, "/loans").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/loans/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/loans/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/loans/**").authenticated()

                        .requestMatchers("/test").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JWTTokenFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}