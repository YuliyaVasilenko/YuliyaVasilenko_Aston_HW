package com.example.user_service.service;

import com.example.common_models.exception.UserNotFoundException;
import com.example.user_service.BaseIntegrationTest;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.kafka.KafkaProducerService;
import com.example.user_service.model.UserEntity;
import com.example.user_service.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 18-04-2026
 * Description: интеграционные тесты для класса UserService (взаимодействие с базой данных)
 */
public class UserServiceDAOTest extends BaseIntegrationTest {

    private final String TEST_NAME = "Test Name";
    private final String TEST_EMAIL = "test@email.com";
    private final int TEST_AGE = 25;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO(TEST_NAME, TEST_EMAIL, TEST_AGE);
    }

    @AfterEach
    void flush() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldCreateUser_ReturnUserDTO() {

        UserDTO result = userService.createUser(userDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getAge()).isEqualTo(TEST_AGE);
    }

    @Test
    void findUserById_ValidId_ReturnOptionalOfUserDTO() {
        UserDTO createdUser = userService.createUser(userDTO);
        List<UserEntity> users = userRepository.findByEmail(createdUser.getEmail());
        Long userId = users.getFirst().getId();

        Optional<UserDTO> foundUser = userService.findUserById(userId);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(TEST_NAME);
        assertThat(foundUser.get().getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(foundUser.get().getAge()).isEqualTo(TEST_AGE);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 999L, -10L})
    void findUserById_NegativeOrZeroId_ThrowUserNotFoundException(long nonExistentId) {

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(nonExistentId));
    }

    @Test
    void findAllUsers_shouldFindAllUsers_ReturnListOfUsers() {
        UserDTO userDTO2 = new UserDTO("Test2 Name", "test2@email.com", 35);
        userDTO = userService.createUser(userDTO);
        userDTO2 = userService.createUser(userDTO2);

        List<UserDTO> result = userService.findAllUsers();

        assertThat(result.size()).isEqualTo(2);

        boolean hasUser1 = result.stream()
                .anyMatch(u -> TEST_NAME.equals(u.getName())
                        && TEST_EMAIL.equals(u.getEmail()) && TEST_AGE == u.getAge());
        boolean hasUser2 = result.stream()
                .anyMatch(u -> "Test2 Name".equals(u.getName())
                        && "test2@email.com".equals(u.getEmail()) && 35 == u.getAge());
        assertThat(hasUser1).isTrue();
        assertThat(hasUser2).isTrue();
    }

    @Test
    void updateUser_shouldUpdateUser() {
        userDTO = userService.createUser(userDTO);
        List<UserEntity> users = userRepository.findByEmail(userDTO.getEmail());
        Long userId = users.getFirst().getId();

        userDTO.setName("NewName");
        userDTO.setEmail("new@example.com");
        userDTO.setAge(75);

        UserDTO updatedUser = userService.updateUser(userId, userDTO);

        assertThat(updatedUser).isNotNull();
        assertThat("NewName").isEqualTo(updatedUser.getName());
        assertThat("new@example.com").isEqualTo(updatedUser.getEmail());
        assertThat(75).isEqualTo(updatedUser.getAge());
    }

    @Test
    void deleteUser_ValidId_Success() {
        userDTO = userService.createUser(userDTO);
        List<UserEntity> users = userRepository.findByEmail(userDTO.getEmail());
        Long userId = users.getFirst().getId();

        assertDoesNotThrow(() -> userService.deleteUser(userId));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 999L, -10L})
    void deleteUser_NegativeOrZeroId_ThrowUserNotFoundException(long nonExistentId) {

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(nonExistentId));
    }
}
