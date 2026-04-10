package view;

import models.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 05-04-2026
 * Description: тесты для класса Viewer
 */
class ViewerTest {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;

    private Viewer viewer;

    private final String command = "save user";

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        viewer = new Viewer();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void showResult_User_shouldShowSuccessMessageWhenOperationSucceeds() {
        UserEntity userEntity = new UserEntity("name", "email@email.ru", 10);
        String expectedOutput = "The operation was successful, save user=" + userEntity + System.lineSeparator();

        viewer.showResult(userEntity, command);

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void showResult_User_shouldShowErrorMessageWhenOperationFails() {
        String expectedOutput = "The operation was wrong, could not save user" + System.lineSeparator();

        viewer.showResult(null, command);

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void showResult_boolean_shouldShowSuccessMessageWhenOperationSucceeds() {
        boolean isSucceed = true;
        String expectedOutput = "The operation was successful, save user" + System.lineSeparator();

        viewer.showResult(isSucceed, command);

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void showResult_boolean_shouldShowErrorMessageWhenOperationFails() {
        boolean isSucceed = false;
        String expectedOutput = "The operation was wrong, could not save user" + System.lineSeparator();

        viewer.showResult(isSucceed, command);

        assertEquals(expectedOutput, outputStream.toString());
    }
}