package com.example.user_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .components(new Components()
                        .schemas(Map.of(
                                "EntityModelUserDTO", createEntityModelUserDTOSchema(),
                                "UserDTO", createUserDTOSchema()
                        ))
                );
    }

    private Schema<?> createEntityModelUserDTOSchema() {
        Schema<Object> schema = new Schema<>();
        schema.type("object");
        schema.addProperty("id", new IntegerSchema().format("int64"));
        schema.addProperty("name", new StringSchema());
        schema.addProperty("email", new StringSchema());
        schema.addProperty("age", new IntegerSchema());
        schema.addProperty("createdAt", new DateTimeSchema());
        schema.addProperty("updatedAt", new DateTimeSchema());

        Schema<Object> linksSchema = new Schema<>();
        linksSchema.type("object");

        Schema<Object> linkSchema = new Schema<>();
        linkSchema.type("object");
        linkSchema.addProperty("href", new StringSchema().format("uri"));

        linksSchema.addProperty("self", linkSchema);
        linksSchema.addProperty("all-users", linkSchema);
        linksSchema.addProperty("update", linkSchema);
        linksSchema.addProperty("delete", linkSchema);

        schema.addProperty("_links", linksSchema);

        return schema;
    }

    private Schema<?> createUserDTOSchema() {
        Schema<Object> schema = new Schema<>();
        schema.type("object");
        schema.addProperty("id", new IntegerSchema().format("int64"));
        schema.addProperty("name", new StringSchema());
        schema.addProperty("email", new StringSchema());
        schema.addProperty("age", new IntegerSchema());
        schema.addProperty("createdAt", new DateTimeSchema());
        schema.addProperty("updatedAt", new DateTimeSchema());
        return schema;
    }
}
