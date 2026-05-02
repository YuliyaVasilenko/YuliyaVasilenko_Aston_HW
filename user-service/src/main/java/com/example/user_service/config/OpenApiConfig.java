package com.example.user_service.config;

import com.example.common_models.exception.ValidationError;
import com.example.user_service.dto.UserDTO;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import java.util.Map;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-04-2026
 * Description: This class contains base settings for OpenApi
 */
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
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
        DateTimeSchema localDateTimeSchema = (DateTimeSchema) new DateTimeSchema()
                .type("string").format("date-time").example("2026-04-27T16:48:53.665Z");

        Components components = new Components()
                .addSchemas("UserRequest", new Schema<UserDTO>()
                        .type("object")
                        .addProperty("name", new Schema<String>().example("Mike"))
                        .addProperty("email", new Schema<String>().example("mike@gmail.com"))
                        .addProperty("age", new Schema<Integer>().example(25)))
                .addSchemas("UserResponse", new Schema<UserDTO>()
                        .type("object")
                        .addProperty("id", new Schema<Long>().example(1L))
                        .addProperty("name", new Schema<String>().example("Mike"))
                        .addProperty("email", new Schema<String>().example("mike@gmail.com"))
                        .addProperty("age", new Schema<Integer>().example(25))
                        .addProperty("created_at", localDateTimeSchema)
                        .addProperty("updated_at", localDateTimeSchema)
                        .addProperty("_links", new Schema<Map<String, Object>>()
                                .type("object")
                                .additionalProperties(new Schema<>()
                                        .type("object")
                                        .addProperty("href", new Schema<String>()))
                                .example(Map.of(
                                        "update", Map.of("href", "/users/1"),
                                        "all-users", Map.of("href", "/users")))))
                .addSchemas("ValidationError", new Schema<ValidationError>()
                        .type("object")
                        .addProperty("statusCode", new Schema<Integer>().type("integer").example(400))
                        .addProperty("message", new Schema<String>().type("string").example("Validation failed"))
                        .addProperty("timestamp", localDateTimeSchema))
                .addSchemas("ValidationErrorList", new ArraySchema()
                        .type("array")
                        .items(new Schema<ValidationError>().$ref("#/components/schemas/ValidationError")))
                .addSchemas("UserNotFound", new Schema<ValidationError>()
                        .type("object")
                        .addProperty("statusCode", new Schema<Integer>().type("integer").example(404))
                        .addProperty("message", new Schema<String>().type("string").example("User not found"))
                        .addProperty("timestamp", localDateTimeSchema));

        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .version("1.0.0")
                        .description("REST API for managing users")
                        .contact(new Contact()
                                .name("Yuliya Vasilenko")))
                .components(components);
    }
}