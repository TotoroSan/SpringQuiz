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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS and disable CSRF
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Add CORS configuration
            .csrf(csrf -> csrf.disable())  // Disable CSRF for simplicity in dev
            
            // Configure login and other routes
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/h2-console/**").permitAll()  // Allow access to H2 console
                .requestMatchers("/admin/**").hasRole("ADMIN")  // Admin role for /admin
                .anyRequest().authenticated()  // All other endpoints require authentication
            )
            .formLogin(form -> form
                .loginPage("/login").permitAll()  // Use Spring Security's default login page
                .defaultSuccessUrl("/quiz", true)  // Redirect after successful login
            )
            .logout(logout -> logout.permitAll());  // Allow logout for everyone

        return http.build();
    }

    // CORS configuration bean to allow specific origins
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);  // Allow credentials (cookies, session IDs, etc.)
        config.addAllowedOrigin("http://localhost:3000");  // Allow this origin only
        config.addAllowedHeader("*");  // Allow all headers
        config.addAllowedMethod("*");  // Allow all methods (POST, GET, etc.)
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    
    // if we had a own security controller and would not use spring security, we would set this configuration 
    //via annotation in the controllerclass
    @Bean 
    public CorsConfigurationSource corsConfigurationSource() {
        // Create a new CorsConfiguration object
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);  // Allow credentials (cookies, etc.)
        config.addAllowedOrigin("http://localhost:3000");  // Specify allowed origin
        config.addAllowedHeader("*");  // Allow all headers
        config.addAllowedMethod("*");  // Allow all HTTP methods (GET, POST, etc.)

        // Register the configuration for all paths ("/**")
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // Apply to all routes

        return source;
    }

    // Dummy in-memory user details for testing
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
            .password("{noop}admin123")
            .roles("ADMIN")
            .build();
        UserDetails user = User.withUsername("user")
            .password("{noop}user123")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(admin, user);
    }
}

