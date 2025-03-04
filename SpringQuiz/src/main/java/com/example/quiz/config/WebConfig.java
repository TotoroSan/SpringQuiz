package com.example.quiz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// used to manage web permissions for the api access 


@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Spring Boot Configuration: If you serve your React app with Spring Boot, you need to ensure all routes are forwarded to the index.html file.
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/quiz").setViewName("forward:/index.html");
        registry.addViewController("/login").setViewName("forward:/index.html");
        // Add other routes here
    }
}

