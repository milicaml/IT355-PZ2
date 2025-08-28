package com.it355pz2.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IT355PZ2 - Job Marketplace API")
                        .description("""
                                Comprehensive REST API for the Job Marketplace application.
                                                                
                                ## Features
                                - **User Management**: Registration, authentication, and profile management
                                - **Job Posting**: Create, update, and manage job listings
                                - **Application System**: Apply for jobs and track application status
                                - **Skill Management**: Add skills with proficiency levels
                                - **Category System**: Organize jobs and skills by categories
                                - **Payment Types**: Support for various payment methods
                                                                
                                ## User Types
                                - **EMPLOYER**: Can post jobs and manage applications
                                - **FREELANCER**: Can apply for jobs and manage skills
                                - **ADMIN**: Full system access and management
                                                                
                                ## Authentication
                                This API uses JWT (JSON Web Token) authentication. Include the JWT token in the Authorization header:
                                ```
                                Authorization: Bearer <jwt-token>
                                ```
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("IT355PZ2 Development Team")
                                .email("support@jobmarketplace.com")
                                .url("https://github.com/"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.jobmarketplace.com")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT token in the format: Bearer <token>")));
    }
}

