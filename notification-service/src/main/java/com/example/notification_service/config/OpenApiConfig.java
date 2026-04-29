package com.example.notification_service.config;

import com.example.common_models.exception.ValidationError;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 29.04.2026
 * Description: This class contains base settings for OpenApi
 */
@Configuration
public class OpenApiConfig {

    /**
     * @ Method Name: customOpenAPI
     * @ Description: Creates and configures an OpenAPI-bean
     * @ param      : []
     * @ return     : io.swagger.v3.oas.models.OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        Components components = new Components()
                .addSchemas("ValidationError", new Schema<ValidationError>()
                        .type("object")
                        .addProperty("statusCode", new Schema<Integer>().type("integer").example(400))
                        .addProperty("message", new Schema<String>().type("string").example("Validation failed"))
                        .addProperty("timestamp", new Schema<LocalDateTime>()
                                .type("string").format("date-time").example("2026-04-27T16:48:53.665Z")));

        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .version("1.0.0")
                        .description("REST API for managing notifications")
                        .contact(new Contact()
                                .name("Yuliya Vasilenko")))
                .components(components);
    }
}
