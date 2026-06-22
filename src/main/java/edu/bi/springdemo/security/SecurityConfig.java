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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // users
                        .requestMatchers(HttpMethod.GET, "/users/me").authenticated()
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
                        // loans
                        .requestMatchers(HttpMethod.GET, "/loans").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/loans/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/loans/user/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/loans/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/loans/borrow").hasRole("READER")

                        .requestMatchers(HttpMethod.PUT, "/loans/*/approve-borrow").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/loans/*/return").hasRole("READER")
                        .requestMatchers(HttpMethod.PUT, "/loans/*/approve-return").hasRole("LIBRARIAN")

                        .requestMatchers("/test").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JWTTokenFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}