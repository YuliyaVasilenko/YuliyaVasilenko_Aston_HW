package com.example.user_service.controller;

import com.example.user_service.dto.UserDTO;
import com.example.user_service.service.UserService;
import com.example.user_service.util.UserControllerAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 10-04-2026
 * Description: REST controller for managing user operations.
 * Provides endpoints for creating, reading, updating and deleting users.
 * All endpoints return appropriate HTTP status codes and include comprehensive logging for monitoring and debugging purposes.
 */
@AllArgsConstructor
@Validated
@Tag(name = "User Management", description = "API for managing user operations")
@RestController
@RequestMapping(path = "/users", produces = "application/hal+json")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    private UserControllerAssembler assembler;

    /**
     * @ Method Name: createUser
     * @ Description: creates a new user
     * @ param      : [com.example.user_service.dto.UserDTO]
     * @ return     : org.springframework.http.ResponseEntity<com.example.user_service.dto.UserDTO>;
     * ResponseEntity containing the created UserDTO with HTTP status 201 (CREATED) if successful
     * and with status 400 (BAD_REQUEST) in case of invalid input data
     */
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user and returns the created UserDTO with fields: " +
                    "name, email, age, date of creation, date of last update",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/UserResponse"))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorList")))
            })
    @PostMapping
    public ResponseEntity<EntityModel<UserDTO>> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "UserDTO with fields name, email, age", required = true,
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UserRequest")))
            @RequestBody @Valid UserDTO userDTO) {
        logger.info("Received CREATE user request. Request data: {}", userDTO);

        UserDTO createdUser = userService.createUser(userDTO);

        EntityModel<UserDTO> model = assembler.toModel(createdUser);

        logger.info("User created successfully. Response: {}, HTTP status: CREATED", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    /**
     * @ Method Name: getUserById
     * @ Description: fetches a user by ID
     * @ param      : [java.lang.Long]
     * @ return     : org.springframework.http.ResponseEntity<com.example.user_service.dto.UserDTO>$
     * ResponseEntity containing UserDTO with HTTP status 200 (OK) if user is found or 404 (NOT_FOUND) if user doesn't exist
     */
    @Operation(
            summary = "Get user by ID",
            description = "Fetches a user by their unique ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found successfully",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/UserResponse"))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorList"))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/UserNotFound")))
            })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> getUserById(
            @Parameter(description = "The user's ID for the search", example = "1", required = true)
            @PathVariable("id") @Positive Long id) {
        logger.info("Received GET user request for ID: {}", id);

        Optional<UserDTO> user = userService.findUserById(id);

        EntityModel<UserDTO> model = assembler.toModel(user.get());

        logger.info("User found successfully. ID: {}, Response: {}", id, user);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    /**
     * @ Method Name: getAllUsers
     * @ Description: extracts all users
     * @ param      : []
     * @ return     : org.springframework.http.ResponseEntity<java.util.List<com.example.user_service.dto.UserDTO>>;
     * ResponseEntity containing list of UserDTO objects with HTTP status 200 (OK) if successful
     * and with status 204 (NO_CONTENT) in case of empty list
     */
    @Operation(
            summary = "Get all users",
            description = "Fetches a list of all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetches list of users",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/UserResponse")))
            })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> getAllUsers() {
        logger.info("Received GET-ALL users request");

        List<UserDTO> users = userService.findAllUsers();

        List<EntityModel<UserDTO>> userResources = users.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserDTO>> collectionModel = CollectionModel.of(userResources);
        collectionModel.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        collectionModel.add(linkTo(methodOn(UserController.class).createUser(new UserDTO())).withRel("create"));

        logger.info("Successfully fetched {} users", users.size());
        return ResponseEntity.ok(collectionModel);
    }

    /**
     * @ Method Name: updateUser
     * @ Description: updates an existing user by ID
     * @ param      : [java.lang.Long, com.example.user_service.dto.UserDTO]
     * @ return     : org.springframework.http.ResponseEntity<com.example.user_service.dto.UserDTO>;
     * ResponseEntity containing updated UserDTO with HTTP status 200 (OK) if successful or 404 (NOT_FOUND) if user doesn't exist
     */
    @Operation(
            summary = "Update a user",
            description = "Updates an existing user by their unique ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/UserResponse"))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorList"))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/UserNotFound")))
            })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> updateUser(
            @Parameter(description = "ID of the user to update", example = "1", required = true)
            @PathVariable("id") @Positive
            Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "UserDTO with fields name, email, age", required = true,
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UserRequest")))
            @RequestBody @Valid UserDTO userDTO) {
        logger.info("Received UPDATE user request for ID: {}. Request data: {}", id, userDTO);

        UserDTO updatedUser = userService.updateUser(id, userDTO);

        EntityModel<UserDTO> model = assembler.toModel(updatedUser);

        logger.info("User updated successfully. ID: {}, Response: {}", id, updatedUser);
        return ResponseEntity.ok(model);
    }

    /**
     * @ Method Name: deleteUser
     * @ Description: deletes a user by ID
     * @ param      : [java.lang.Long]
     * @ return     : void; ResponseEntity with HTTP status 204 (NO_CONTENT) if deletion is successful, 404 (NOT_FOUND) if user doesn't exist
     */
    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by their unique ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/UserNotFound")))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID of the user to delete", example = "1", required = true)
            @PathVariable("id") @NotNull @Positive Long id) {
        logger.info("Received DELETE user request for ID: {}", id);

        userService.deleteUser(id);

        logger.info("User deleted successfully. ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
