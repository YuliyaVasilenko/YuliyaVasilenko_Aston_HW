package menu;

import dao.UserDAO;
import input.CheckWriting;
import models.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import view.Viewer;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 04-04-2026
 * Description: тесты для класса MainManager
 */
@ExtendWith(MockitoExtension.class)
class MainManagerTest {
    @Spy
    MainManager mainManager;
    @Mock
    UserDAO mockUserDAO;
    @Mock
    CheckWriting mockCheckWriting;
    @Mock
    Viewer mockViewer;
    String name = "TestName";
    String email = "test@test.ru";
    int age = 10;
    UserEntity expectedUserEntity;

    @BeforeEach
    void setUp() {
        try {
            Field userDAOField = mainManager.getClass().getDeclaredField("userDAO");
            userDAOField.setAccessible(true);
            userDAOField.set(mainManager, mockUserDAO);

            Field checkerField = mainManager.getClass().getDeclaredField("checker");
            checkerField.setAccessible(true);
            checkerField.set(mainManager, mockCheckWriting);

            Field viewerField = mainManager.getClass().getDeclaredField("viewer");
            viewerField.setAccessible(true);
            viewerField.set(mainManager, mockViewer);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
        expectedUserEntity = new UserEntity(name, email, age);
    }

    @Test
    void create_correctNameEmailAgeDAOCreation_ReturnUser() {
        when(mockCheckWriting.checkWord("name")).thenReturn(name);
        when(mockCheckWriting.checkEmail()).thenReturn(email);
        when(mockCheckWriting.checkNumber("age", 1, 100)).thenReturn(age);
        when(mockUserDAO.create(expectedUserEntity)).thenReturn(expectedUserEntity);

        UserEntity result = mainManager.create();

        assertEquals(expectedUserEntity, result);
        verify(mockCheckWriting, times(1)).checkWord("name");
        verify(mockCheckWriting,times(1)).checkEmail();
        verify(mockCheckWriting, times(1)).checkNumber("age", 1, 100);
        verify(mockUserDAO, only()).create(expectedUserEntity);
    }

    @Test
    void create_NullName_ReturnNull() {
        when(mockCheckWriting.checkWord("name")).thenReturn(null);

        UserEntity result = mainManager.create();

        assertNull(result);
        verify(mockCheckWriting, only()).checkWord("name");
        verify(mockCheckWriting, never()).checkEmail();
        verify(mockCheckWriting, never()).checkNumber(anyString(), anyInt(), anyInt());
        verify(mockUserDAO, never()).create(any());
    }

    @Test
    void create_NullEmail_ReturnNull() {
        when(mockCheckWriting.checkWord("name")).thenReturn(name);
        when(mockCheckWriting.checkEmail()).thenReturn(null);

        UserEntity result = mainManager.create();

        assertNull(result);
        verify(mockCheckWriting, times(1)).checkWord("name");
        verify(mockCheckWriting, times(1)).checkEmail();
        verify(mockCheckWriting, never()).checkNumber(anyString(), anyInt(), anyInt());
        verify(mockUserDAO, never()).create(any());
    }

    @Test
    void create_IncorrectAge_ReturnNull() {
        when(mockCheckWriting.checkWord("name")).thenReturn(name);
        when(mockCheckWriting.checkEmail()).thenReturn(email);
        when(mockCheckWriting.checkNumber("age", 1, 100)).thenReturn(-1);

        UserEntity result = mainManager.create();

        assertNull(result);
        verify(mockCheckWriting, times(1)).checkWord("name");
        verify(mockCheckWriting, times(1)).checkEmail();
        verify(mockCheckWriting, times(1)).checkNumber("age", 1, 100);
        verify(mockUserDAO, never()).create(any());
    }

    @Test
    void create_IncorrectUserDAOCreation_ReturnNull() {
        when(mockCheckWriting.checkWord("name")).thenReturn(name);
        when(mockCheckWriting.checkEmail()).thenReturn(email);
        when(mockCheckWriting.checkNumber("age", 1, 100)).thenReturn(age);
        when(mockUserDAO.create(any(UserEntity.class))).thenReturn(null);

        UserEntity result = mainManager.create();

        assertNull(result);
        verify(mockCheckWriting, times(1)).checkWord("name");
        verify(mockCheckWriting, times(1)).checkEmail();
        verify(mockCheckWriting, times(1)).checkNumber("age", 1, 100);
        verify(mockUserDAO, only()).create(expectedUserEntity);
    }

    @Test
    void update_UserNotFound_ReturnNull() {
        doReturn(null).when(mainManager).find();

        UserEntity result = mainManager.update();

        assertNull(result);
        verify(mainManager, times(1)).find();
        verify(mockViewer, never()).askFieldToUpdate();
        verify(mockCheckWriting, never()).checkNumber(anyString(), anyInt(), anyInt());
        verify(mockUserDAO, never()).update(any());
    }

    @Test
    void update_NotSelectFieldToUpdate_ReturnNull() {
        doReturn(expectedUserEntity).when(mainManager).find();
        when(mockCheckWriting.checkNumber("field to update", 1, 3)).thenReturn(0);

        UserEntity result = mainManager.update();

        assertNull(result);
        verify(mainManager, times(1)).find();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockCheckWriting, only()).checkNumber("field to update", 1, 3);
        verify(mockUserDAO, never()).update(any());
    }

    @Test
    void update_SelectUpdateName_ReturnUser() {
        doReturn(expectedUserEntity).when(mainManager).find();
        when(mockCheckWriting.checkNumber("field to update", 1, 3)).thenReturn(1);
        String newName = "NewTestName";
        UserEntity updatedUserEntity = new UserEntity(newName, email, age);
        when(mockCheckWriting.checkWord("name")).thenReturn(newName);
        when(mockUserDAO.update(expectedUserEntity)).thenReturn(updatedUserEntity);

        UserEntity result = mainManager.update();

        assertNotNull(result);
        assertEquals(newName, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(age, result.getAge());

        verify(mainManager, times(1)).find();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockCheckWriting, times(1)).checkNumber("field to update", 1, 3);
        verify(mockCheckWriting, times(1)).checkWord("name");
        verify(mockUserDAO, times(1)).update(expectedUserEntity);
    }

    @Test
    void update_SelectUpdateNameThenCancel_ReturnNull() {
        doReturn(expectedUserEntity).when(mainManager).find();
        when(mockCheckWriting.checkNumber("field to update", 1, 3)).thenReturn(1);
        when(mockCheckWriting.checkWord("name")).thenReturn(null);

        UserEntity result = mainManager.update();

        assertNull(result);
        verify(mainManager, times(1)).find();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockCheckWriting, times(1)).checkNumber("field to update", 1, 3);
        verify(mockCheckWriting, times(1)).checkWord("name");
        verify(mockUserDAO, never()).update(any());
    }

    @Test
    void update_SelectUpdateEmail_ReturnUser() {
        doReturn(expectedUserEntity).when(mainManager).find();
        when(mockCheckWriting.checkNumber("field to update", 1, 3)).thenReturn(2);
        String newEmail = "newTestemail@test.ru";
        UserEntity updatedUserEntity = new UserEntity(name, newEmail, age);
        when(mockCheckWriting.checkWord("email")).thenReturn(newEmail);
        when(mockUserDAO.update(expectedUserEntity)).thenReturn(updatedUserEntity);

        UserEntity result = mainManager.update();

        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
        assertEquals(name, result.getName());
        assertEquals(age, result.getAge());

        verify(mainManager, times(1)).find();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockCheckWriting, times(1)).checkNumber("field to update", 1, 3);
        verify(mockCheckWriting, times(1)).checkWord("email");
        verify(mockUserDAO, times(1)).update(expectedUserEntity);
    }

    @Test
    void update_SelectUpdateEmailThenCancel_ReturnNull() {
        doReturn(expectedUserEntity).when(mainManager).find();
        when(mockCheckWriting.checkNumber("field to update", 1, 3)).thenReturn(2);
        when(mockCheckWriting.checkWord("email")).thenReturn(null);

        UserEntity result = mainManager.update();

        assertNull(result);
        verify(mainManager, times(1)).find();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockCheckWriting, times(1)).checkNumber("field to update", 1, 3);
        verify(mockCheckWriting, times(1)).checkWord("email");
        verify(mockUserDAO, never()).update(any());
    }

    @Test
    void update_SelectUpdateAge_ReturnUser() {
        doReturn(expectedUserEntity).when(mainManager).find();
        when(mockCheckWriting.checkNumber("field to update", 1, 3)).thenReturn(3);
        int newAge = 30;
        UserEntity updatedUserEntity = new UserEntity(name, email, newAge);
        when(mockCheckWriting.checkNumber("age", 1, 100)).thenReturn(age);
        when(mockUserDAO.update(expectedUserEntity)).thenReturn(updatedUserEntity);

        UserEntity result = mainManager.update();

        assertNotNull(result);
        assertEquals(newAge, result.getAge());
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());

        verify(mainManager, times(1)).find();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockCheckWriting, times(1)).checkNumber("field to update", 1, 3);
        verify(mockCheckWriting, times(1)).checkNumber("age", 1, 100);
        verify(mockUserDAO, times(1)).update(expectedUserEntity);
    }

    @Test
    void update_SelectUpdateAgeThenCancel_ReturnNull() {
        doReturn(expectedUserEntity).when(mainManager).find();
        when(mockCheckWriting.checkNumber("field to update", 1, 3)).thenReturn(3);
        when(mockCheckWriting.checkNumber("age", 1, 100)).thenReturn(-1);

        UserEntity result = mainManager.update();

        assertNull(result);
        verify(mainManager, times(1)).find();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockCheckWriting, times(1)).checkNumber("field to update", 1, 3);
        verify(mockCheckWriting, times(1)).checkNumber("age", 1, 100);
        verify(mockUserDAO, never()).update(any());
    }

    @Test
    void update_IncorrectUserDAOCreation_ReturnNull() {
        doReturn(expectedUserEntity).when(mainManager).find();
        when(mockCheckWriting.checkNumber("field to update", 1, 3)).thenReturn(1);
        String newName = "NewTestName";
        when(mockCheckWriting.checkWord("name")).thenReturn(newName);
        when(mockUserDAO.update(any(UserEntity.class))).thenReturn(null);

        UserEntity result = mainManager.update();

        assertNull(result);

        verify(mainManager, times(1)).find();
        verify(mockViewer, times(1)).askFieldToUpdate();
        verify(mockCheckWriting, times(1)).checkNumber("field to update", 1, 3);
        verify(mockCheckWriting, times(1)).checkWord("name");
        verify(mockUserDAO, times(1)).update(expectedUserEntity);
    }

    @Test
    void selectId_Proceed_OneAskIdAndOneInput() {
        when(mockCheckWriting.checkNumber("id", 1, 1000)).thenReturn(10);

        mainManager.selectId();

        verify(mockViewer, only()).askId();
        verify(mockCheckWriting, only()).checkNumber(anyString(), anyInt(), anyInt());
    }
}