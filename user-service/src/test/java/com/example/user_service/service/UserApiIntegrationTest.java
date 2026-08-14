package com.example.user_service.service;

import com.example.user_service.BaseIntegrationTest;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 12-04-2026
 * Description: интеграционные тесты для проверки работы всех компонентов:
 * UserController, UserRepository, KafkaProducerService, UserService
 */
public class UserApiIntegrationTest extends BaseIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String TEST_NAME = "TestName";
    private final String TEST_EMAIL = "test@test.ru";
    private final int TEST_AGE = 10;

    @Autowired
    UserRepository userRepository;

    @Autowired
    KafkaConsumer kafkaConsumer;

    @LocalServerPort
    private int port;

    private ResponseEntity<UserDTO> response;

    private UserDTO userDTO;

    private String path;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO(TEST_NAME, TEST_EMAIL, TEST_AGE);
        path = "http://localhost:" + port + "/api/users";
    }

    @AfterEach
    void flush() {
        userRepository.deleteAll();
        kafkaConsumer.reset();
    }

    private Long createTestUser(UserDTO user) {
        ResponseEntity<UserDTO> response = restTemplate.postForEntity(path, user, UserDTO.class);

        return userRepository.findByEmail(user.getEmail()).getFirst().getId();
    }

    @Test
    void createUser_Success() {
        response = restTemplate.postForEntity(path, userDTO, UserDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_NAME, response.getBody().getName());
        assertEquals(TEST_EMAIL, response.getBody().getEmail());
        assertEquals(TEST_AGE, response.getBody().getAge());
    }

    @Test
    void getUserById_Success() {
        Long userId = createTestUser(userDTO);

        response = restTemplate.getForEntity(path + "/" + userId, UserDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_NAME, response.getBody().getName());
        assertEquals(TEST_EMAIL, response.getBody().getEmail());
        assertEquals(TEST_AGE, response.getBody().getAge());
    }

    @Test
    void updateUser_Success() {
        Long userId = createTestUser(userDTO);

        UserDTO updatedUserDTO = new UserDTO("NewName", "newEmail@test.ru", 25);
        HttpEntity<UserDTO> request = new HttpEntity<>(updatedUserDTO);

        ResponseEntity<UserDTO> updatedResponse = restTemplate.exchange(
                path + "/" + userId, HttpMethod.PUT, request, UserDTO.class);

        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        assertNotNull(updatedResponse.getBody());
        assertEquals("NewName", updatedResponse.getBody().getName());
        assertEquals("newEmail@test.ru", updatedResponse.getBody().getEmail());
        assertEquals(25, updatedResponse.getBody().getAge());
    }

    @Test
    void deleteUser_Success() {
        Long userId = createTestUser(userDTO);

        ResponseEntity<Void> response = restTemplate.exchange(path + "/" + userId,
                HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

}
