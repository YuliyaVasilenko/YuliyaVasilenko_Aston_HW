package menu;

import input.CheckWriting;
import models.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.Viewer;

import java.util.List;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 27-03-2026
 * Description: this class describes how the application menu works
 */
public class MenuManager {
    private static final Logger logger = LoggerFactory.getLogger(MenuManager.class);
    private MainManager mainManager = new MainManager();
    private CheckWriting checkWriting = new CheckWriting();
    private Viewer viewer = new Viewer();

    /**
     * @ Method Name: proceed
     * @ Description: the main method of operation of the menu, the general order of application processing
     * @ param -> return: [] [] -> void
     */
    public void proceed() {
        logger.info("The application has started");
        Menu menu;
        do {
            viewer.saySelectOperation();
            viewer.showOperations();
            menu = selectOperation();
            logger.info("The operation was selected: {}", menu.name());
            if (menu != Menu.EXIT) {
                switchOperation(menu, mainManager);
                logger.info("The operation was proceed: {}", menu.name());
            }
        } while (menu != Menu.EXIT);
        logger.info("The exit from the program was selected");
    }

    /**
     * @ Method Name: switchOperation
     * @ Description: the method that describes which procedure is called for each 'button'
     * @ param -> return: [menu.Menu, menu.MainManager] [menu, mainManager] -> void
     */
    public void switchOperation(Menu menu, MainManager manager) {
        switch (menu) {
            case CREATE -> {
                UserEntity userEntity = manager.create();
                viewer.showResult(userEntity, menu.getDescription());
            }
            case READ -> {
                UserEntity userEntity = manager.find();
                viewer.showResult(userEntity, menu.getDescription());
            }
            case READ_ALL -> {
                List<UserEntity> userEntities = manager.findAll();
                viewer.showResult(userEntities);
            }
            case UPDATE -> {
                UserEntity userEntity = manager.update();
                viewer.showResult(userEntity, menu.getDescription());
            }
            case DELETE -> {
                boolean isDeleted = manager.delete();
                viewer.showResult(isDeleted, menu.getDescription());
            }
        }
    }

    /**
     * @ Method Name: selectOperation
     * @ Description: the method that asks which 'button' is selected;
     * connects the selection in console (int number) and the Menu value
     * @ param -> return: [] [] -> menu.Menu
     */
    public Menu selectOperation() {
        int ordinal = checkWriting.checkNumber("operation",
                1, Menu.values().length) - 1;
        if (ordinal == -1) ordinal = Menu.EXIT.ordinal();
        return Menu.getOrdinal(ordinal);
    }
}
