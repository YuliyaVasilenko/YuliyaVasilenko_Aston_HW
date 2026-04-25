package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.model.UserEntity;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 12-04-2026
 * Description: тесты для класса UserService
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    UserEntity userEntity;

    UserDTO userDTO;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Test Name");
        userEntity.setEmail("test@email.com");
        userEntity.setAge(30);
        userDTO = new UserDTO("Test Name", "test@email.com", 30);
    }

    @Test
    void createUser_Success_ReturnUserDTO() {
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDTO result = userService.createUser(userDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Name", result.getName());
        assertEquals("test@email.com", result.getEmail());
        assertEquals(30, result.getAge());

        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(modelMapper).map(any(UserEntity.class), eq(UserDTO.class));
    }

    @Test
    void findUserById_Success_ReturnUserDTO() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        Optional<UserDTO> result = userService.findUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        assertEquals("Test Name", result.get().getName());

        verify(userRepository, times(1)).findById(userId);
        verify(modelMapper, times(1)).map(userEntity, UserDTO.class);
    }

    @Test
    void findUserById_NotFound_ThrowUserNotFoundException() {
        Long nonExistentId = 999L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(nonExistentId));

        verify(userRepository, times(1)).findById(nonExistentId);
        verify(modelMapper, never()).map(any(), eq(UserDTO.class));
    }

    @Test
    void findAllUsers_Success_ReturnListAllUsers() {
        UserEntity userEntity2 = new UserEntity();
        userEntity2.setId(2L);
        userEntity2.setName("Test2");
        userEntity2.setEmail("test2@example.com");
        userEntity2.setAge(25);
        List<UserEntity> userEntities = Arrays.asList(userEntity, userEntity2);

        UserDTO userDTO2 = new UserDTO("Test2", "test2@example.com", 25);
        userDTO2.setId(2L);
        List<UserDTO> expectedDTOs = Arrays.asList(userDTO, userDTO2);

        when(userRepository.findAll()).thenReturn(userEntities);
        when(modelMapper.map(userEntity, UserDTO.class)).thenReturn(userDTO);
        when(modelMapper.map(userEntity2, UserDTO.class)).thenReturn(userDTO2);

        List<UserDTO> result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userDTO, result.get(0));
        assertEquals(userDTO2, result.get(1));
        assertEquals(expectedDTOs, result);

        verify(userRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(UserEntity.class), eq(UserDTO.class));
    }

    @Test
    void findAllUsers_Success_ReturnListEmpty() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDTO> result = userService.findAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_Success_ReturnUpdatedUser() {
        UserEntity updatedEntity = new UserEntity();
        Long userId = 10L;
        updatedEntity.setId(2L);
        updatedEntity.setName("Updated Name");
        updatedEntity.setEmail("updated@example.com");
        updatedEntity.setAge(35);

        UserDTO updateDTO = new UserDTO("Updated Name", "updated@example.com", 35);
        updateDTO.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(updatedEntity);
        when(modelMapper.map(updatedEntity, UserDTO.class)).thenReturn(updateDTO);

        UserDTO result = userService.updateUser(userId, updateDTO);

        assertNotNull(result);
        assertEquals(updateDTO, result);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(userEntity);
        verify(modelMapper, times(1)).map(updatedEntity, UserDTO.class);
    }

    @Test
    void updateUser_NotFound_ThrowUserNotFoundException() {
        Long nonExistentId = 999L;
        userDTO.setId(nonExistentId);

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(nonExistentId, userDTO));

        verify(userRepository, times(1)).findById(nonExistentId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_NotFound_ThrowUserNotFoundException() {
        Long nonExistentId = 999L;

        when(userRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(nonExistentId));

        verify(userRepository, times(1)).existsById(nonExistentId);
        verify(userRepository, never()).deleteById(anyLong());
    }
}