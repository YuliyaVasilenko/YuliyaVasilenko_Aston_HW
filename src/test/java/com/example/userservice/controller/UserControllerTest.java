package com.example.userservice.controller;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 11-04-2026
 * Description: тесты для класса UserController
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDTO userDTO;

    private String validJson;

    private Long userId;

    private String TEST_NAME;

    private String TEST_EMAIL;

    private int TEST_AGE;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO("TestName", "test@test.com", 35);
        userDTO.setId(1L);
        validJson = "{\"name\":\"TestName\",\"email\":\"test@test.com\",\"age\":35}";
        userId = 1L;
        TEST_NAME = "TestName";
        TEST_EMAIL = "test@test.com";
        TEST_AGE = 35;
        objectMapper = new ObjectMapper();
    }

    @Test
    void createUser_Success_ReturnCreated() throws Exception {
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/hw4/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.age").value(TEST_AGE));
    }

    @Test
    void createUser_ValidationError_ReturnBadRequest() throws Exception {
        String invalidJson = "{\"TEST_NAME\":\"\",\"TEST_EMAIL\":\"invalid-TEST_EMAIL\",\"TEST_AGE\":-10}";

        mockMvc.perform(post("/hw4/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUserById_Success_ReturnUser() throws Exception {
        when(userService.findUserById(userId)).thenReturn(Optional.of(userDTO));

        mockMvc.perform(get("/hw4/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.age").value(TEST_AGE));
    }

    @Test
    void getUserById_NotFound_ReturnNotFound() throws Exception {
        Long userId = 999L;
        when(userService.findUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/hw4/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_Success_ReturnListAllUsers() throws Exception {
        UserDTO userDTO2 = new UserDTO("Jane Smith", "jane@example.com", 25);
        userDTO2.setId(2L);
        List<UserDTO> users = Arrays.asList(userDTO, userDTO2);
        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/hw4/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value(userDTO))
                .andExpect(jsonPath("$[1]").value(userDTO2));
    }

    @Test
    void getAllUsers_NoUsers_ReturnListEmpty() throws Exception {
        when(userService.findAllUsers()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/hw4/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void updateUser_Success_ReturnUpdatedUser() throws Exception {
        UserDTO updatedUser = new UserDTO("Updated Name", "updated@example.com", 25);
        updatedUser.setId(userId);

        when(userService.updateUser(any(Long.class), any(UserDTO.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/hw4/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void updateUser_NotFound_ReturnNotFound() throws Exception {
        Long userId = 999L;
        userDTO.setId(userId);

        when(userService.updateUser(any(Long.class), any(UserDTO.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/hw4/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ValidationError_ReturnBadRequest() throws Exception {
        UserDTO invalidDTO = new UserDTO("", "invalid-TEST_EMAIL", -5);
        invalidDTO.setId(userId);

        mockMvc.perform(put("/hw4/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/hw4/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

}