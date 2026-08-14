package com.example.user_service.controller;

import com.example.common_models.exception.UserNotFoundException;
import com.example.common_models.handler.GlobalExceptionHandler;
import com.example.user_service.assembler.UserControllerAssembler;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.hateoas.autoconfigure.HypermediaAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
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
 * Description: tests for the UserController class
 */
@ContextConfiguration(classes = {
        UserController.class,
        UserControllerAssembler.class,
        GlobalExceptionHandler.class,
        HypermediaAutoConfiguration.class
})
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private final String TEST_NAME = "TestName";
    private final String TEST_EMAIL = "test@test.ru";
    private final int TEST_AGE = 35;
    private final Long ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserDTO userDTO;

    private String path;

    public MockHttpServletRequestBuilder postJson(String uri, UserDTO body) {
        String json = new JsonMapper().writeValueAsString(body);
        return post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
    }

    public MockHttpServletRequestBuilder putJson(String uri, Object body) {
        String json = new JsonMapper().writeValueAsString(body);
        return put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
    }

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO(TEST_NAME, TEST_EMAIL, TEST_AGE);
        userDTO.setId(ID);
        path = "http://localhost/users";
    }

    @Test
    void createUser_Success_ReturnCreatedWithHalLinks() throws Exception {
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(postJson(path, userDTO))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.age").value(TEST_AGE))

                .andExpect(jsonPath("$._links.self.href").value(path + "/" + ID))
                .andExpect(jsonPath("$._links.all-users.href").value(path));
    }

    @Test
    void createUser_ValidationError_ReturnBadRequest() throws Exception {
        userDTO = new UserDTO("", "", -10);

        mockMvc.perform(postJson(path, userDTO))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_Success_ReturnUserWithHalLinks() throws Exception {
        when(userService.findUserById(ID)).thenReturn(Optional.of(userDTO));

        mockMvc.perform(get(path + "/" + ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.age").value(TEST_AGE))

                .andExpect(jsonPath("$._links.self.href").value(path + "/" + ID))
                .andExpect(jsonPath("$._links.all-users.href").value(path));
    }

    @Test
    void getUserById_NotFound_ReturnNotFound() throws Exception {
        when(userService.findUserById(anyLong())).thenThrow(new UserNotFoundException());

        mockMvc.perform(get(path + "/" + ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_Success_ReturnCollectionWithHalLinks() throws Exception {
        UserDTO userDTO2 = new UserDTO("TestName2", "test2@example.com", 27);
        Long id2 = 2L;
        userDTO2.setId(id2);
        List<UserDTO> users = List.of(userDTO, userDTO2);
        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))

                .andExpect(jsonPath("$._embedded.userDTOList[0].id").value(ID))
                .andExpect(jsonPath("$._embedded.userDTOList[0].name").value(TEST_NAME))
                .andExpect(jsonPath("$._embedded.userDTOList[0].email").value(TEST_EMAIL))
                .andExpect(jsonPath("$._embedded.userDTOList[0].age").value(TEST_AGE))

                .andExpect(jsonPath("$._embedded.userDTOList[1].id").value(id2))
                .andExpect(jsonPath("$._embedded.userDTOList[1].name").value("TestName2"))
                .andExpect(jsonPath("$._embedded.userDTOList[1].email").value("test2@example.com"))
                .andExpect(jsonPath("$._embedded.userDTOList[1].age").value(27))

                .andExpect(jsonPath("$._embedded.userDTOList[1]._links.self.href").value(path + "/" + id2))
                .andExpect(jsonPath("$._embedded.userDTOList[0]._links.all-users.href").value(path))
                .andExpect(jsonPath("$._embedded.userDTOList[1]._links.self.href").value(path + "/" + id2))
                .andExpect(jsonPath("$._embedded.userDTOList[1]._links.all-users.href").value(path));
    }

    @Test
    void updateUser_Success_ReturnUpdatedUser() throws Exception {
        UserDTO updatedUser = new UserDTO("Updated Name", "updated@example.com", 25);
        updatedUser.setId(ID);

        when(userService.updateUser(any(Long.class), any(UserDTO.class))).thenReturn(updatedUser);

        mockMvc.perform(putJson(path + "/" + ID, userDTO))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.age").value(25))

                .andExpect(jsonPath("$._links.self.href").value(path + "/" + ID))
                .andExpect(jsonPath("$._links.all-users.href").value(path));
    }

    @Test
    void updateUser_NotFound_ReturnNotFound() throws Exception {
        when(userService.updateUser(anyLong(), any(UserDTO.class)))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(putJson(path + "/" + ID, userDTO))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void updateUser_ValidationError_ReturnBadRequest() throws Exception {
        UserDTO invalidDTO = new UserDTO("", "invalid-TEST_EMAIL", -5);

        mockMvc.perform(putJson(path + "/" + ID, invalidDTO))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_Success_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete(path + "/" + ID))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(ID);
    }

    @Test
    void deleteUser_NotFound_ReturnNotFound() throws Exception {
        doThrow(new UserNotFoundException()).when(userService).deleteUser(anyLong());

        mockMvc.perform(delete(path + "/" + ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

}