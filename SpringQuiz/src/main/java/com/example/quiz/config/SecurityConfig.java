package com.example.quiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // test - should be removable 
@EnableWebSecurity // This includes @Configuration and sets up Spring Security
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // Allow all requests without authentication
            )
            .csrf(csrf -> csrf.disable())  // Disable CSRF protection (important for H2 console and APIs)
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())  // Allow frames for H2 console
            )
            .httpBasic(httpBasic -> httpBasic.disable());  // Disable HTTP Basic authentication

        return http.build();
    }
}
