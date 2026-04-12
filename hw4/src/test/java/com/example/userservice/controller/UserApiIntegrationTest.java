package com.example.userservice.controller;

import com.example.userservice.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 12-04-2026
 * Description: интеграционные тесты для UserController
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserApiIntegrationTest {

    ResponseEntity<UserDTO> response;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO("TestName", "test@test.ru", 10);
    }

    @Test
    void createUser() {
        response = restTemplate.postForEntity("http://localhost:" + port + "/hw4/users", userDTO, UserDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void getUserById() {
        response = restTemplate.postForEntity("http://localhost:" + port + "/hw4/users", userDTO, UserDTO.class);
        Long userId = response.getBody().getId();
        response = restTemplate.getForEntity("http://localhost:" + port + "/hw4/users/" + userId, UserDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("TestName", response.getBody().getName());
    }

    @Test
    void updateUser() {
        response = restTemplate.postForEntity("http://localhost:" + port + "/hw4/users", userDTO, UserDTO.class);
        Long userId = response.getBody().getId();

        UserDTO updatedUserDTO = new UserDTO("NewName", "newEmail@test.ru", 25);
        HttpEntity<UserDTO> request = new HttpEntity<>(updatedUserDTO);

        ResponseEntity<UserDTO> updatedResponse = restTemplate.exchange(
                "http://localhost:" + port + "/hw4/users/" + userId, HttpMethod.PUT, request, UserDTO.class);

        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        assertEquals("NewName", updatedResponse.getBody().getName());
    }

}
