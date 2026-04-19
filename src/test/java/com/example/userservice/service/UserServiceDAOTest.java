package com.example.userservice.service;

import com.example.userservice.BaseIntegrationTest;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.kafka.KafkaProducerService;
import com.example.userservice.kafka.UserEvent;
import com.example.userservice.model.UserEntity;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;


/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 18-04-2026
 * Description: интеграционные тесты для класса UserService (взаимодействие с базой данных)
 */
@SpringBootTest
public class UserServiceDAOTest extends BaseIntegrationTest {

    private final String TEST_NAME = "Test Name";
    private final String TEST_EMAIL = "test@email.com";
    private final int TEST_AGE = 25;

    @Autowired
    @InjectMocks
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO(TEST_NAME, TEST_EMAIL, TEST_AGE);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldCreateUser_ReturnUserDTO() {
        doNothing().when(kafkaProducerService).sendMessage(any(UserEvent.class));

        UserDTO result = userService.createUser(userDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getAge()).isEqualTo(TEST_AGE);

        UserEntity savedUser = userRepository.findById(result.getId())
                .orElseThrow(() -> new AssertionError("User not found in database"));
        assertThat(savedUser.getName()).isEqualTo(TEST_NAME);
        assertThat(savedUser.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(savedUser.getAge()).isEqualTo(TEST_AGE);
    }

    @Test
    void findUserById_ValidId_ReturnOptionalOfUserDTO() {
        UserDTO createdUser = userService.createUser(userDTO);

        Optional<UserDTO> foundUser = userService.findUserById(createdUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(createdUser.getId());
        assertThat(foundUser.get().getName()).isEqualTo(TEST_NAME);
        assertThat(foundUser.get().getEmail()).isEqualTo(TEST_EMAIL);
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
        Long id1 = userDTO.getId();
        userDTO2 = userService.createUser(userDTO2);
        Long id2 = userDTO2.getId();

        List<UserDTO> result = userService.findAllUsers();

        assertThat(result.size()).isEqualTo(2);

        boolean hasUser1 = result.stream()
                .anyMatch(u -> Objects.equals(u.getId(), id1) && TEST_NAME.equals(u.getName())
                        && TEST_EMAIL.equals(u.getEmail()) && TEST_AGE == u.getAge());
        boolean hasUser2 = result.stream()
                .anyMatch(u -> Objects.equals(u.getId(), id2) && "Test2 Name".equals(u.getName())
                        && "test2@email.com".equals(u.getEmail()) && 35 == u.getAge());
        assertThat(hasUser1).isTrue();
        assertThat(hasUser2).isTrue();
    }

    @Test
    void updateUser_shouldUpdateUser() {
        userDTO = userService.createUser(userDTO);

        userDTO.setName("NewName");
        userDTO.setEmail("new@example.com");
        userDTO.setAge(75);

        UserDTO updatedUser = userService.updateUser(userDTO.getId(), userDTO);

        assertThat(updatedUser).isNotNull();
        assertThat("NewName").isEqualTo(updatedUser.getName());
        assertThat("new@example.com").isEqualTo(updatedUser.getEmail());
        assertThat(75).isEqualTo(updatedUser.getAge());
    }

    @Test
    void deleteUser_ValidId_Success() {
        doNothing().when(kafkaProducerService).sendMessage(any(UserEvent.class));
        userDTO = userService.createUser(userDTO);
        Long id = userDTO.getId();

        assertDoesNotThrow(() -> userService.deleteUser(id));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 999L, -10L})
    void deleteUser_NegativeOrZeroId_ThrowUserNotFoundException(long nonExistentId) {

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(nonExistentId));
    }
}
