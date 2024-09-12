package com.example.quiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Using the newer syntax for disabling CSRF and permitting all requests
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // Allow all requests
            )
            .csrf(csrf -> csrf.disable());  // Disable CSRF

        return http.build();
    }
}
