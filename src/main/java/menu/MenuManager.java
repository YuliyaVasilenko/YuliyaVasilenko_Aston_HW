package menu;

import input.CheckWriting;
import models.User;
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

    /**
     * @ Method Name: proceed
     * @ Description: the main method of operation of the menu, the general order of application processing
     * @ param -> return: [] [] -> void
     */
    public void proceed() {
        logger.info("The application has started");
        Menu menu;
        MainManager manager = new MainManager();
        do {
            Viewer.saySelectOperation();
            Viewer.showOperations();
            menu = selectOperation();
            logger.info("The operation was selected: {}", menu.name());
            if (menu != Menu.EXIT) {
                switchOperation(menu, manager);
                logger.info("The operation was proceed: {}", menu.name());
            }
        } while (menu != Menu.EXIT);
        logger.info("The exit from the program was selected");
    }

    /**
     * @ Method Name: switchOperation
     * @ Description: the method that describes which procedure is called for each 'button'
     * @ param -> return: [menu.Menu, menu.MainManager] [menu, manager] -> void
     */
    public void switchOperation(Menu menu, MainManager manager) {
        switch (menu) {
            case CREATE -> {
                User user = manager.create();
                Viewer.showResult(user, menu.getDescription());
            }
            case READ -> {
                User user = manager.find();
                Viewer.showResult(user, menu.getDescription());
            }
            case READ_ALL -> {
                List<User> users = manager.findAll();
                Viewer.showResult(users);
            }
            case UPDATE -> {
                User user = manager.update();
                Viewer.showResult(user, menu.getDescription());
            }
            case DELETE -> {
                boolean isDeleted = manager.delete();
                Viewer.showResult(isDeleted, menu.getDescription());
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
        int ordinal = new CheckWriting().checkNumber("operation",
                1, Menu.values().length) - 1;
        if (ordinal == -1) ordinal = Menu.EXIT.ordinal();
        return Menu.getOrdinal(ordinal);
    }
}
