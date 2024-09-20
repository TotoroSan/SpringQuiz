package com.example.quiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @SuppressWarnings("removal")
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for H2 console and APIs
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**").disable())

                // Allow access to the H2 console and permit all, disable frame options for H2
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/h2-console/**").permitAll()  // Allow all access to H2 console
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Only admins can access /admin/**
                        .anyRequest().authenticated())  // Require authentication for all other endpoints

                // Disable X-Frame-Options for H2 console (to allow the H2 console UI to be embedded in iframes)
                .headers(headers -> headers.frameOptions().disable())

                // Enable HTTP Basic authentication
                .httpBasic();  // Enables HTTP Basic authentication

        return http.build();
    }



    @Bean
    public UserDetailsService userDetailsService() {
        // Create two users: admin and user. Only for test purposes.
        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin123")  // {noop} means no password encoding
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password("{noop}user123")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}
