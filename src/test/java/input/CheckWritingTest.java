package input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 03-04-2026
 * Description: тесты для класса CheckWriting
 */
@ExtendWith(MockitoExtension.class)
class CheckWritingTest {
    private final int EXIT_NUM = CheckWriting.getEXIT();

    private final String EXIT = String.valueOf(CheckWriting.getEXIT());

    @Spy
    private CheckWriting checker;

    @Mock
    private Scanner mockScanner;

    @BeforeEach
    void setUp() {
        try {
            Field scannerField = checker.getClass().getDeclaredField("scanner");
            scannerField.setAccessible(true);
            scannerField.set(checker, mockScanner);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 25, 78})
    void checkNumber_validNumberInRange_ReturnInput(int input) {
        try (MockedStatic<Scanner> mocked = mockStatic(Scanner.class)) {
            mocked.when(mockScanner::nextLine).thenReturn(String.valueOf(input));

            int result = checker.checkNumber("testNumber", 1, 100);

            assertEquals(input, result);
        }
    }

    @Test
    void checkNumber_SelectExit_ReturnExit() {
        try (MockedStatic<Scanner> mocked = mockStatic(Scanner.class)) {
            mocked.when(mockScanner::nextLine).thenReturn(EXIT);

            int result = checker.checkNumber("testNumber", 1, 100);

            assertEquals(EXIT_NUM, result);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-5, 101, 404})
    void checkNumber_validNumberOutOfRange_ReturnExitInsteadInput(int input) {
        try (MockedStatic<Scanner> mocked = mockStatic(Scanner.class)) {
            mocked.when(mockScanner::nextLine).thenReturn(String.valueOf(input)).thenReturn(EXIT);

            int result = checker.checkNumber("testNumber", 1, 100);

            assertNotEquals(input, result);
            assertEquals(EXIT_NUM, result);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaa", "a1b", "12Ab", "", "   ", "\t", "@", "@*#"})
    void checkNumber_InvalidNumberFormat_ReturnExitInsteadInput(String input) {
        try (MockedStatic<Scanner> mocked = mockStatic(Scanner.class)) {
            mocked.when(mockScanner::nextLine).thenReturn(input).thenReturn(EXIT);

            int result = checker.checkNumber("testNumber", 1, 100);

            assertNotEquals(input, String.valueOf(result));
            assertEquals(EXIT_NUM, result);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaa", "a1b", "12Ab", "@", "@*#"})
    void checkWord_correctWord_ReturnInput(String input) {
        try (MockedStatic<Scanner> mocked = mockStatic(Scanner.class)) {
            mocked.when(mockScanner::nextLine).thenReturn(input);

            String result = checker.checkWord("testWord");

            assertEquals(input, result);
        }
    }

    @Test
    void checkWord_SelectExit_ReturnNull() {
        try (MockedStatic<Scanner> mocked = mockStatic(Scanner.class)) {
            mocked.when(mockScanner::nextLine).thenReturn(EXIT);

            String result = checker.checkWord("testWord");

            assertNull(result);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    void checkWord_BlankWord_ReturnNullInsteadInput(String input) {
        try (MockedStatic<Scanner> mocked = mockStatic(Scanner.class)) {
            mocked.when(mockScanner::nextLine).thenReturn(input).thenReturn(EXIT);

            String result = checker.checkWord("testWord");

            assertNotEquals(input, result);
            assertNull(result);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"ann@ya.ru", "kitty@yandex.ru", "fr@bk.ru", "k@mail.com"})
    void checkEmail_correctEmail_ReturnInput(String input) {
        doReturn(input).when(checker).checkWord("email");

        String result = checker.checkEmail();

        assertEquals(input, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaa", "a1b", "12Ab", "", "   ", "\t", "@", "@*#", "a@bb"})
    void checkEmail_IncorrectEmail_ReturnNull(String input) {
        doReturn(input, null).when(checker).checkWord("email");

        String result = checker.checkEmail();

        assertNotEquals(input, result);
        assertNull(result);
    }
}