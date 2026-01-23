package com.hong.thebaker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI theBakerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("The Baker API Documentation")
                        .description("API for Managing Orders, Inventory, and Staff Dashboard")
                        .version("v1.0.0"));
    }
}