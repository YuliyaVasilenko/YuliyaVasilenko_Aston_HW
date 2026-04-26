package com.example.user_service.config;

import com.example.common_models.exception.ValidationError;
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
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import java.util.List;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-04-2026
 * Description: This class contains base settings for OpenApi
 */
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@Configuration
public class OpenApiConfig {

    @Value("${server.port}")
    private String port;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .version("1.0.0")
                        .description("REST API for managing users")
                        .contact(new Contact()
                                .name("Yuliya Vasilenko")))
                .servers(List.of(
                        new Server().url(port).description("User Service Application")))
                .paths(new Paths()
                        .addPathItem("/api/users", new PathItem()
                                .post(new Operation()
                                        .responses(new ApiResponses()
                                                .addApiResponse("400", new ApiResponse()
                                                        .description("Validation failed")
                                                        .content(new Content()
                                                                .addMediaType("application/json", new MediaType()
                                                                        .schema(new Schema<ValidationError>().$ref("#/components/schemas/ValidationError")))))
                                                .addApiResponse("404", new ApiResponse()
                                                        .description("Resource not found")
                                                        .content(new Content()
                                                                .addMediaType("application/json", new MediaType()
                                                                        .schema(new Schema<ValidationError>().$ref("#/components/schemas/ValidationError")))))))))
                ;
    }
}
