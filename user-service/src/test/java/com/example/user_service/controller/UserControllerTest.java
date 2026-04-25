package com.example.user_service.controller;

import com.example.common_models.exception.UserNotFoundException;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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
public class UserControllerTest {

    private final String TEST_NAME = "TestName";
    private final String TEST_EMAIL = "test@test.ru";
    private final int TEST_AGE = 35;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserDTO userDTO;
    private String path;
    private Long userId;

    public static MockHttpServletRequestBuilder postJson(String uri, Object body) {
        try {
            String json = new ObjectMapper().writeValueAsString(body);
            return post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static MockHttpServletRequestBuilder putJson(String uri, Object body) {
        try {
            String json = new ObjectMapper().writeValueAsString(body);
            return put(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        userId = 1L;
        userDTO = new UserDTO(TEST_NAME, TEST_EMAIL, TEST_AGE);
        path = "/api/users";
    }

    @Test
    void createUser_Success_ReturnCreatedUser() throws Exception {
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(postJson(path, userDTO))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.age").value(TEST_AGE));
    }

    @Test
    void createUser_ValidationError_ReturnBadRequest() throws Exception {
        userDTO = new UserDTO("", "", -10);

        mockMvc.perform(postJson(path, userDTO))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_Success_ReturnUser() throws Exception {
        when(userService.findUserById(userId)).thenReturn(Optional.of(userDTO));

        mockMvc.perform(get(path + "/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.age").value(TEST_AGE));
    }

    @Test
    void getUserById_NotFound_ReturnNotFound() throws Exception {
        Long userId = 999L;
        when(userService.findUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get(path + "/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_Success_ReturnListAllUsers() throws Exception {
        UserDTO userDTO2 = new UserDTO("TestName2", "test2@example.com", 27);
        List<UserDTO> users = Arrays.asList(userDTO, userDTO2);
        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].name").value(TEST_NAME))
                .andExpect(jsonPath("$[0].email").value(TEST_EMAIL))
                .andExpect(jsonPath("$[0].age").value(TEST_AGE))

                .andExpect(jsonPath("$[1].name").value("TestName2"))
                .andExpect(jsonPath("$[1].email").value("test2@example.com"))
                .andExpect(jsonPath("$[1].age").value(27));
    }

    @Test
    void getAllUsers_NoUsers_ReturnListEmpty() throws Exception {
        when(userService.findAllUsers()).thenReturn(new ArrayList<>());

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void updateUser_Success_ReturnUpdatedUser() throws Exception {
        UserDTO updatedUser = new UserDTO("Updated Name", "updated@example.com", 25);

        when(userService.updateUser(any(Long.class), any(UserDTO.class))).thenReturn(updatedUser);

        mockMvc.perform(putJson(path + "/" + userId, userDTO))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void updateUser_NotFound_ReturnNotFound() throws Exception {
        long userId = 999L;

        when(userService.updateUser(any(Long.class), any(UserDTO.class)))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(putJson(path + "/" + userId, userDTO))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ValidationError_ReturnBadRequest() throws Exception {
        UserDTO invalidDTO = new UserDTO("", "invalid-TEST_EMAIL", -5);

        mockMvc.perform(putJson(path + "/" + userId, invalidDTO))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete(path + "/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

}