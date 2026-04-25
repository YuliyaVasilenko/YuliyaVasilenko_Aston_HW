package com.example.userservice.cli;

import com.example.userservice.cli.validator.ConsoleValidator;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 04-04-2026
 * Description: тесты для класса ConsoleMenu
 */
@ExtendWith(MockitoExtension.class)
class ConsoleMenuTest {

    private final String TEST_NAME = "TestName";

    private final String TEST_EMAIL = "test@test.ru";

    private final int TEST_AGE = 10;

    @Spy
    @InjectMocks
    private ConsoleMenu consoleMenu;

    @Mock
    private UserService mockUserService;

    @Mock
    private ConsoleValidator mockConsoleValidator;

    @Mock
    private ConsoleMenuViewer mockViewer;

    private UserDTO userDTO;

    private static IntStream argsProvidedFactory_selectOperation() {
        return IntStream.rangeClosed(1, Menu.values().length);
    }

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO(TEST_NAME, TEST_EMAIL, TEST_AGE);
    }

    @Test
    void proceed_ExitImmediately_OneMenuLoop() {
        doReturn(Menu.EXIT).when(consoleMenu).selectOperation();

        consoleMenu.proceed();

        verify(mockViewer, times(1)).saySelectOperation();
        verify(mockViewer, times(1)).showOperations();
        verify(consoleMenu).selectOperation();
        verify(consoleMenu, never()).switchOperation(any(Menu.class));
    }

    @Test
    void proceed_OneOperationThenExit_TwoMenuLoop() {
        doReturn(Menu.CREATE, Menu.EXIT).when(consoleMenu).selectOperation();

        consoleMenu.proceed();

        verify(mockViewer, times(2)).saySelectOperation();
        verify(mockViewer, times(2)).showOperations();
        verify(consoleMenu, times(2)).selectOperation();
        verify(consoleMenu, times(1)).switchOperation(Menu.CREATE);
    }

    @ParameterizedTest
    @MethodSource("argsProvidedFactory_selectOperation")
    void selectOperation_ValidInput_ReturnMenu(int input) {
        when(mockConsoleValidator.checkNumber("operation", 1, Menu.values().length))
                .thenReturn(input);

        Menu result = consoleMenu.selectOperation();

        assertEquals(Menu.values()[input - 1], result);
    }

    @Test
    void selectOperation_InvalidInputOrExit_ReturnExit() {
        when(mockConsoleValidator.checkNumber("operation", 1, Menu.values().length))
                .thenReturn(ConsoleValidator.getEXIT());

        Menu result = consoleMenu.selectOperation();

        assertEquals(Menu.EXIT, result);
    }

    @Test
    void selectId_Proceed_OneAskIdAndOneInput() {
        when(mockConsoleValidator.checkNumber("id", 1, 1000)).thenReturn(10);

        consoleMenu.selectId();

        verify(mockViewer, only()).askId();
        verify(mockConsoleValidator, only()).checkNumber(anyString(), anyInt(), anyInt());
    }

    @Test
    void create_correctNameEmailAgeServiceCreation_Success() {
        when(mockConsoleValidator.checkWord("name")).thenReturn(TEST_NAME);
        when(mockConsoleValidator.checkEmail()).thenReturn(TEST_EMAIL);
        when(mockConsoleValidator.checkNumber("age", 1, 100)).thenReturn(TEST_AGE);
        when(mockUserService.createUser(userDTO)).thenReturn(userDTO);

        consoleMenu.create();

        verify(mockConsoleValidator, times(1)).checkWord("name");
        verify(mockConsoleValidator, times(1)).checkEmail();
        verify(mockConsoleValidator, times(1)).checkNumber("age", 1, 100);
        verify(mockUserService, only()).createUser(userDTO);
        verify(mockViewer, times(1)).showResult(Optional.of(userDTO), Menu.CREATE.getDescription());
    }

    @Test
    void create_NullName_NotCreate() {
        when(mockConsoleValidator.checkWord("name")).thenReturn(null);

        consoleMenu.create();

        verify(mockConsoleValidator, only()).checkWord("name");
        verify(mockConsoleValidator, never()).checkEmail();
        verify(mockConsoleValidator, never()).checkNumber(anyString(), anyInt(), anyInt());
        verify(mockUserService, never()).createUser(any());
        verify(mockViewer, times(1)).showResult(Optional.empty(), Menu.CREATE.getDescription());
    }

    @Test
    void create_NullEmail_NotCreate() {
        when(mockConsoleValidator.checkWord("name")).thenReturn(TEST_NAME);
        when(mockConsoleValidator.checkEmail()).thenReturn(null);

        consoleMenu.create();

        verify(mockConsoleValidator, times(1)).checkWord("name");
        verify(mockConsoleValidator, times(1)).checkEmail();
        verify(mockConsoleValidator, never()).checkNumber(anyString(), anyInt(), anyInt());
        verify(mockUserService, never()).createUser(any());
        verify(mockViewer, times(1)).showResult(Optional.empty(), Menu.CREATE.getDescription());
    }

    @Test
    void create_IncorrectAge_NotCreate() {
        when(mockConsoleValidator.checkWord("name")).thenReturn(TEST_NAME);
        when(mockConsoleValidator.checkEmail()).thenReturn(TEST_EMAIL);
        when(mockConsoleValidator.checkNumber("age", 1, 100)).thenReturn(-1);

        consoleMenu.create();

        verify(mockConsoleValidator, times(1)).checkWord("name");
        verify(mockConsoleValidator, times(1)).checkEmail();
        verify(mockConsoleValidator, times(1)).checkNumber("age", 1, 100);
        verify(mockUserService, never()).createUser(any());
        verify(mockViewer, times(1)).showResult(Optional.empty(), Menu.CREATE.getDescription());
    }

    @Test
    void create_IncorrectUserServiceCreation_NotCreate() {
        when(mockConsoleValidator.checkWord("name")).thenReturn(TEST_NAME);
        when(mockConsoleValidator.checkEmail()).thenReturn(TEST_EMAIL);
        when(mockConsoleValidator.checkNumber("age", 1, 100)).thenReturn(TEST_AGE);
        when(mockUserService.createUser(any(UserDTO.class))).thenReturn(null);

        consoleMenu.create();

        verify(mockConsoleValidator, times(1)).checkWord("name");
        verify(mockConsoleValidator, times(1)).checkEmail();
        verify(mockConsoleValidator, times(1)).checkNumber("age", 1, 100);
        verify(mockUserService, only()).createUser(userDTO);
        verify(mockViewer, times(1)).showResult(Optional.empty(), Menu.CREATE.getDescription());
    }

    @Test
    void read_ValidId_Success() {
        Long userId = 1L;
        doReturn(userId).when(consoleMenu).selectId();
        when(mockUserService.findUserById(userId)).thenReturn(Optional.of(userDTO));

        consoleMenu.read();

        verify(consoleMenu, times(1)).selectId();
        verify(mockUserService, times(1)).findUserById(userId);
        verify(mockViewer, times(1)).showResult(Optional.of(userDTO), Menu.READ.getDescription());
    }

    @Test
    void read_InvalidId_NotFound() {
        Long userId = 1000L;
        doReturn(userId).when(consoleMenu).selectId();
        when(mockUserService.findUserById(userId)).thenThrow(UserNotFoundException.class);

        consoleMenu.read();

        verify(consoleMenu, times(1)).selectId();
        verify(mockUserService, times(1)).findUserById(userId);
        verify(mockViewer, times(1)).showResult(Optional.empty(), Menu.READ.getDescription());
    }

    @Test
    void readAll_ReturnListAllUsers() {
        UserDTO userDTO2 = new UserDTO("TestName2", "test2@email.ru", 44);
        List<UserDTO> users = List.of(userDTO, userDTO2);

        when(mockUserService.findAllUsers()).thenReturn(users);

        consoleMenu.readAll();

        verify(mockUserService, times(1)).findAllUsers();
        verify(mockViewer, times(1)).showResult(users);
    }

    @Test
    void update_NotSelectFieldToUpdate_NotUpdate() {
        doReturn(1L).when(consoleMenu).selectId();
        when(mockConsoleValidator.checkNumber("field to update", 1, 3)).thenReturn(0);

        consoleMenu.update();

        verify(consoleMenu, times(1)).selectId();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockConsoleValidator, only()).checkNumber("field to update", 1, 3);
        verify(mockUserService, never()).updateUser(anyLong(), any(UserDTO.class));
        verify(mockViewer, times(1)).showResult(Optional.empty(), Menu.UPDATE.getDescription());
    }

    @Test
    void update_SelectUpdateName_Success() {
        Long userId = 1L;
        doReturn(userId).when(consoleMenu).selectId();
        when(mockConsoleValidator.checkNumber("field to update", 1, 3)).thenReturn(1);

        String newName = "NewTestName";
        when(mockConsoleValidator.checkWord("name")).thenReturn(newName);

        UserDTO updatedUserDTO = new UserDTO(newName, null, null);
        userDTO.setName(newName);
        when(mockUserService.updateUser(userId, updatedUserDTO)).thenReturn(userDTO);

        consoleMenu.update();

        verify(consoleMenu, times(1)).selectId();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockConsoleValidator, times(1)).checkNumber("field to update", 1, 3);
        verify(mockConsoleValidator, times(1)).checkWord("name");
        verify(mockUserService, times(1)).updateUser(userId, updatedUserDTO);
        verify(mockViewer, times(1)).showResult(Optional.of(userDTO), Menu.UPDATE.getDescription());
    }

    @Test
    void update_SelectUpdateNameThenCancel_NotUpdate() {
        Long userId = 1L;
        doReturn(userId).when(consoleMenu).selectId();
        when(mockConsoleValidator.checkNumber("field to update", 1, 3)).thenReturn(1);

        when(mockConsoleValidator.checkWord("name")).thenReturn(null);

        consoleMenu.update();

        verify(consoleMenu, times(1)).selectId();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockConsoleValidator, times(1)).checkNumber("field to update", 1, 3);
        verify(mockConsoleValidator, times(1)).checkWord("name");
        verify(mockUserService, never()).updateUser(anyLong(), any(UserDTO.class));
        verify(mockViewer, times(1)).showResult(Optional.empty(), Menu.UPDATE.getDescription());
    }

    @Test
    void update_SelectUpdateEmail_Success() {
        Long userId = 1L;
        doReturn(userId).when(consoleMenu).selectId();
        when(mockConsoleValidator.checkNumber("field to update", 1, 3)).thenReturn(2);

        String newEmail = "newTestEmail@test.ru";
        when(mockConsoleValidator.checkEmail()).thenReturn(newEmail);

        UserDTO updatedUserDTO = new UserDTO(null, newEmail, null);
        userDTO.setEmail(newEmail);
        when(mockUserService.updateUser(userId, updatedUserDTO)).thenReturn(userDTO);

        consoleMenu.update();

        verify(consoleMenu, times(1)).selectId();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockConsoleValidator, times(1)).checkNumber("field to update", 1, 3);
        verify(mockConsoleValidator, times(1)).checkEmail();
        verify(mockUserService, times(1)).updateUser(userId, updatedUserDTO);
        verify(mockViewer, times(1)).showResult(Optional.of(userDTO), Menu.UPDATE.getDescription());
    }

    @Test
    void update_SelectUpdateEmailThenCancel_NotUpdate() {
        Long userId = 1L;
        doReturn(userId).when(consoleMenu).selectId();
        when(mockConsoleValidator.checkNumber("field to update", 1, 3)).thenReturn(2);

        when(mockConsoleValidator.checkEmail()).thenReturn(null);

        consoleMenu.update();

        verify(consoleMenu, times(1)).selectId();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockConsoleValidator, times(1)).checkNumber("field to update", 1, 3);
        verify(mockConsoleValidator, times(1)).checkEmail();
        verify(mockUserService, never()).updateUser(anyLong(), any(UserDTO.class));
        verify(mockViewer, times(1)).showResult(Optional.empty(), Menu.UPDATE.getDescription());
    }

    @Test
    void update_SelectUpdateAge_Success() {
        Long userId = 1L;
        doReturn(userId).when(consoleMenu).selectId();
        when(mockConsoleValidator.checkNumber("field to update", 1, 3)).thenReturn(3);

        Integer newAge = 30;
        when(mockConsoleValidator.checkNumber("age", 1, 100)).thenReturn(newAge);

        UserDTO updatedUserDTO = new UserDTO(null, null, newAge);
        userDTO.setAge(newAge);
        when(mockUserService.updateUser(userId, updatedUserDTO)).thenReturn(userDTO);

        consoleMenu.update();

        verify(consoleMenu, times(1)).selectId();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockConsoleValidator, times(1)).checkNumber("field to update", 1, 3);
        verify(mockConsoleValidator, times(1)).checkNumber("age", 1, 100);
        verify(mockUserService, times(1)).updateUser(userId, updatedUserDTO);
        verify(mockViewer, times(1)).showResult(Optional.of(userDTO), Menu.UPDATE.getDescription());
    }

    @Test
    void update_SelectUpdateAgeThenCancel_NotUpdate() {
        Long userId = 1L;
        doReturn(userId).when(consoleMenu).selectId();
        when(mockConsoleValidator.checkNumber("field to update", 1, 3)).thenReturn(3);

        when(mockConsoleValidator.checkNumber("age", 1, 100)).thenReturn(0);

        consoleMenu.update();

        verify(consoleMenu, times(1)).selectId();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockConsoleValidator, times(1)).checkNumber("field to update", 1, 3);
        verify(mockConsoleValidator, times(1)).checkNumber("age", 1, 100);
        verify(mockUserService, never()).updateUser(anyLong(), any(UserDTO.class));
        verify(mockViewer, times(1)).showResult(Optional.empty(), Menu.UPDATE.getDescription());
    }

    @Test
    void update_IncorrectUserServiceUpdating_NotUpdate() {
        Long userId = 1000L;
        doReturn(userId).when(consoleMenu).selectId();
        when(mockConsoleValidator.checkNumber("field to update", 1, 3)).thenReturn(1);

        String newName = "NewTestName";
        when(mockConsoleValidator.checkWord("name")).thenReturn(newName);

        UserDTO updatedUserDTO = new UserDTO(newName, null, null);
        doThrow(UserNotFoundException.class).when(mockUserService).updateUser(userId, updatedUserDTO);

        consoleMenu.update();

        verify(consoleMenu, times(1)).selectId();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockConsoleValidator, times(1)).checkNumber("field to update", 1, 3);
        verify(mockConsoleValidator, times(1)).checkWord("name");
        verify(mockUserService, times(1)).updateUser(userId, updatedUserDTO);
        verify(mockViewer, times(1)).showResult(Optional.empty(), Menu.UPDATE.getDescription());
    }

    @Test
    void delete_ValidId_Success() {
        Long userId = 1L;
        doReturn(userId).when(consoleMenu).selectId();
        doNothing().when(mockUserService).deleteUser(userId);

        consoleMenu.delete();

        verify(consoleMenu, times(1)).selectId();
        verify(mockUserService, times(1)).deleteUser(userId);
    }

    @Test
    void delete_InvalidId_NotFound() {
        Long userId = 1000L;
        doReturn(userId).when(consoleMenu).selectId();
        doThrow(UserNotFoundException.class).when(mockUserService).deleteUser(userId);

        consoleMenu.delete();

        verify(consoleMenu, times(1)).selectId();
        verify(mockUserService, times(1)).deleteUser(userId);
    }
}