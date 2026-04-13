package com.example.userservice.controller;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 10-04-2026
 * Description: REST controller for managing user operations.
 * Provides endpoints for creating, reading, updating and deleting users.
 * All endpoints return appropriate HTTP status codes and include comprehensive logging for monitoring and debugging purposes.
 */
@RestController
@RequestMapping("/hw4/users")
@AllArgsConstructor
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    /**
     * @ Method Name: createUser
     * @ Description: creates a new user
     * @ param      : [com.example.userservice.dto.UserDTO]
     * @ return     : org.springframework.http.ResponseEntity<com.example.userservice.dto.UserDTO>;
     * ResponseEntity containing the created UserDTO with HTTP status 201 (CREATED) if successful
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
        logger.info("Received CREATE user request. Request data: {}", userDTO);

        UserDTO createdUser = userService.createUser(userDTO);

        logger.info("User created successfully. Response: {}, HTTP status: CREATED", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * @ Method Name: getUserById
     * @ Description: extracts a user by ID
     * @ param      : [java.lang.Long]
     * @ return     : org.springframework.http.ResponseEntity<com.example.userservice.dto.UserDTO>$
     * ResponseEntity containing UserDTO with HTTP status 200 (OK) if user is found or 404 (NOT_FOUND) if user doesn't exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") @NotNull @Positive Long id) {
        logger.info("Received GET user request for ID: {}", id);

        Optional<UserDTO> user = userService.findUserById(id);

        if (user.isPresent()) {
            logger.info("User found successfully. ID: {}, Response: {}", id, user);
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            logger.warn("User not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @ Method Name: getAllUsers
     * @ Description: extracts all users
     * @ param      : []
     * @ return     : org.springframework.http.ResponseEntity<java.util.List<com.example.userservice.dto.UserDTO>>;
     * ResponseEntity containing list of UserDTO objects with HTTP status 200 (OK) if successful;
     * Returns empty list if no users exist.
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.info("Received GET-ALL users request");

        List<UserDTO> users = userService.findAllUsers();

        logger.info("Successfully fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * @ Method Name: updateUser
     * @ Description: updates an existing user by ID
     * @ param      : [java.lang.Long, com.example.userservice.dto.UserDTO]
     * @ return     : org.springframework.http.ResponseEntity<com.example.userservice.dto.UserDTO>;
     * ResponseEntity containing updated UserDTO with HTTP status 200 (OK) if successful or 404 (NOT_FOUND) if user doesn't exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") @NotNull @Positive Long id,
                                              @RequestBody @Valid UserDTO userDTO) {
        logger.info("Received UPDATE user request for ID: {}. Request data: {}", id, userDTO);

        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            logger.info("User updated successfully. ID: {}, Response: {}", id, updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            logger.warn("User not found for update with ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * @ Method Name: deleteUser
     * @ Description: deletes a user by ID
     * @ param      : [java.lang.Long]
     * @ return     : void; ResponseEntity with HTTP status 204 (NO_CONTENT) if deletion is successful, 404 (NOT_FOUND) if user doesn't exist
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") @NotNull @Positive Long id) {
        logger.info("Received DELETE user request for ID: {}", id);

        userService.deleteUser(id);

        logger.info("User deleted successfully. ID: {}", id);
    }
}
