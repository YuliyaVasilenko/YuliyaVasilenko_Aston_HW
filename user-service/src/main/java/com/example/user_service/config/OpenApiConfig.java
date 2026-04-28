package com.example.user_service.config;

import com.example.common_models.exception.ValidationError;
import com.example.user_service.dto.UserDTO;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-04-2026
 * Description: This class contains base settings for OpenApi
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .version("1.0.0")
                        .description("REST API for managing users")
                        .contact(new Contact()
                                .name("Yuliya Vasilenko")))
                .tags(List.of(new Tag()
                        .name("User Management")
                        .description("API for managing user operations")))
                .components(new Components()
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
                                .addProperty("created_at", new Schema<LocalDateTime>().type("string").format("date-time")
                                        .example("2026-04-27T16:48:53.665Z"))
                                .addProperty("updated_at", new Schema<LocalDateTime>().type("string").format("date-time")
                                        .example("2026-04-27T16:48:53.665Z"))
                                .addProperty("_links", new Schema<Map<String, Object>>()
                                        .type("object")
                                        .additionalProperties(new Schema<>()
                                                .type("object")
                                                .addProperty("href", new Schema<String>()))
                                        .example(Map.of(
                                                "update", Map.of("href", "/users/1"),
                                                "all-users", Map.of("href", "/users")))))
                        .addSchemas("ValidationErrorList", new Schema<List<ValidationError>>()
                                .type("array")
                                .items(new Schema<ValidationError>()
                                        .type("object")
                                        .addProperty("statusCode", new Schema<Integer>().type("integer").example(400))
                                        .addProperty("message", new Schema<String>().type("string")
                                                .example("Validation failed: field 'email' is invalid"))
                                        .addProperty("timestamp", new Schema<LocalDateTime>().type("string").format("date-time").example("2025-01-10T12:30:45"))))
                        .addSchemas("UserNotFound", new Schema<ValidationError>()
                                .type("object")
                                .addProperty("statusCode", new Schema<Integer>().type("integer").example(404))
                                .addProperty("message", new Schema<String>().type("string").example("User not found"))
                                .addProperty("timestamp", new Schema<LocalDateTime>().type("string").format("date-time").example("2025-01-10T12:30:45"))))
                .paths(new Paths()
                        .addPathItem("/api/users", new PathItem()
                                .post(new Operation()
                                        .responses(new ApiResponses()
                                                .addApiResponse("400", new ApiResponse()
                                                        .description("Validation failed")
                                                        .content(new Content()
                                                                .addMediaType("application/json", new MediaType()
                                                                        .schema(new Schema<>().$ref("#/components/schemas/ValidationErrorList")))))
                                                .addApiResponse("404", new ApiResponse()
                                                        .description("User not found")
                                                        .content(new Content()
                                                                .addMediaType("application/json", new MediaType()
                                                                        .schema(new Schema<>().$ref("#/components/schemas/UserNotFound")))))))));
    }
}
