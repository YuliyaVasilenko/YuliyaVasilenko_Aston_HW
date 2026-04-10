package menu;

import input.CheckWriting;
import models.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import view.Viewer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 04-04-2026
 * Description: тесты для класса MenuManager
 */
@ExtendWith(MockitoExtension.class)
class MenuManagerTest {
    @Spy
    private MenuManager menuManager;

    @Mock
    private MainManager mockMainManager;

    @Mock
    private CheckWriting mockCheckWriting;

    @Mock
    private Viewer mockViewer;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        try {
            Field mainManagerField = menuManager.getClass().getDeclaredField("mainManager");
            mainManagerField.setAccessible(true);
            mainManagerField.set(menuManager, mockMainManager);

            Field checkWritingField = menuManager.getClass().getDeclaredField("checkWriting");
            checkWritingField.setAccessible(true);
            checkWritingField.set(menuManager, mockCheckWriting);

            Field viewerField = menuManager.getClass().getDeclaredField("viewer");
            viewerField.setAccessible(true);
            viewerField.set(menuManager, mockViewer);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println(e.getMessage());
        }

        userEntity = new UserEntity("TestUser", "test@test.ru", 1);
    }

    @Test
    void proceed_ExitImmediately_OneMenuLoop() {
        doReturn(Menu.EXIT).when(menuManager).selectOperation();

        menuManager.proceed();

        verify(mockViewer, times(1)).saySelectOperation();
        verify(mockViewer, times(1)).showOperations();
        verify(menuManager).selectOperation();
        verify(menuManager, never()).switchOperation(any(), any());
    }

    @Test
    void proceed_OneOperationThenExit_TwoMenuLoop() {
        doReturn(Menu.CREATE, Menu.EXIT).when(menuManager).selectOperation();

        menuManager.proceed();

        verify(mockViewer, times(2)).saySelectOperation();
        verify(mockViewer, times(2)).showOperations();
        verify(menuManager, times(2)).selectOperation();
        verify(menuManager, times(1)).switchOperation(Menu.CREATE, mockMainManager);
    }

    @Test
    void switchOperation_SelectCreate_SwitchCreate() {
        when(mockMainManager.create()).thenReturn(userEntity);

        menuManager.switchOperation(Menu.CREATE, mockMainManager);

        verify(mockMainManager).create();
        verify(mockViewer).showResult(userEntity, Menu.CREATE.getDescription());
    }

    @Test
    void switchOperation_SelectRead_SwitchRead() {
        when(mockMainManager.find()).thenReturn(userEntity);

        menuManager.switchOperation(Menu.READ, mockMainManager);

        verify(mockMainManager).find();
        verify(mockViewer).showResult(userEntity, Menu.READ.getDescription());
    }

    @Test
    void switchOperation_SelectReadAll_SwitchReadAll() {
        List<UserEntity> listUserEntities = List.of(userEntity, new UserEntity("TestUser2", "test2@test2.ru", 1));
        when(mockMainManager.findAll()).thenReturn(listUserEntities);

        menuManager.switchOperation(Menu.READ_ALL, mockMainManager);

        verify(mockMainManager).findAll();
        verify(mockViewer).showResult(listUserEntities);
    }

    @Test
    void switchOperation_SelectUpdate_SwitchUpdate() {
        when(mockMainManager.update()).thenReturn(userEntity);

        menuManager.switchOperation(Menu.UPDATE, mockMainManager);

        verify(mockMainManager).update();
        verify(mockViewer).showResult(userEntity, Menu.UPDATE.getDescription());
    }

    @Test
    void switchOperation_SelectDelete_SwitchDelete() {
        when(mockMainManager.delete()).thenReturn(true);

        menuManager.switchOperation(Menu.DELETE, mockMainManager);

        verify(mockMainManager).delete();
        verify(mockViewer).showResult(true, Menu.DELETE.getDescription());
    }

    @ParameterizedTest
    @MethodSource("argsProvidedFactory_selectOperation")
    void selectOperation_ValidInput_ReturnMenu(int input) {
        when(mockCheckWriting.checkNumber("operation", 1, Menu.values().length))
                .thenReturn(input);

        Menu result = menuManager.selectOperation();

        assertEquals(Menu.values()[input - 1], result);
    }

    static IntStream argsProvidedFactory_selectOperation() {
        return IntStream.rangeClosed(1, Menu.values().length);
    }

    @Test
    void selectOperation_InvalidInputOrExit_ReturnExit() {
        when(mockCheckWriting.checkNumber("operation", 1, Menu.values().length))
                .thenReturn(CheckWriting.getEXIT());

        Menu result = menuManager.selectOperation();

        assertEquals(Menu.EXIT, result);
    }
}