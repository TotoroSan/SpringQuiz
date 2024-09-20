
package com.example.quiz.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// this is not necessary if we use standard.
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Quiz API")
                .version("0.1")
                .description("This is a sample Quiz API documentation using Swagger with Spring Boot"))
            .addSecurityItem(new SecurityRequirement().addList("basicScheme"))
            		.components(new Components().addSecuritySchemes("basicScheme",
                    new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")));
    }
}


// Swagger is awesome for automated documentation and integration testing. all controller classes are automatically documented.