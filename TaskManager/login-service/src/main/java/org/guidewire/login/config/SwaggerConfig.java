package org.guidewire.login.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Login Service API")
                        .version("1.0")
                        .description("API documentation for Login Service"))
                .externalDocs(new ExternalDocumentation()
                        .description("Full API Documentation")
                        .url("https://example.com/api-docs.yaml")); // 🔹 Use remote URL
    }
}
