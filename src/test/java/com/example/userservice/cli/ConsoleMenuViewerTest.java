package com.example.userservice.cli;

import com.example.userservice.dto.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 05-04-2026
 * Description: тесты для класса Viewer
 */
class ConsoleMenuViewerTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;

    private final String command = "save user";

    private ConsoleMenuViewer viewer;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        viewer = new ConsoleMenuViewer();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void showResult_shouldShowSuccessMessageWhenOperationSucceeds() {
        Optional<UserDTO> userDTO = Optional.of(new UserDTO("name", "email@email.ru", 10));
        String expectedOutput = "The operation was successful, save user=" + userDTO.get() + System.lineSeparator();

        viewer.showResult(userDTO, command);

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void showResult_shouldShowErrorMessageWhenOperationFails() {
        Optional<UserDTO> userDTO = Optional.empty();
        String expectedOutput = "The operation was wrong, could not save user" + System.lineSeparator();

        viewer.showResult(userDTO, command);

        assertEquals(expectedOutput, outputStream.toString());
    }
}