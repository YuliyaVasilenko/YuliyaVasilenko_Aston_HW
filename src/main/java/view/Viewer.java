package view;

import menu.Menu;
import models.UserEntity;

import java.util.List;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-03-2026
 * Description: the class that is responsible for outputting information to the console
 */
public class Viewer {
    /**
     * @ Method Name: showOperations
     * @ Description: printing all operations which that are in the Menu
     * @ param -> return: [] [] -> void
     */
    public void showOperations() {
        for (Menu menu : Menu.values()) {
            System.out.println(menu);
        }
    }

    /**
     * @ Method Name: saySelectOperation
     * @ Description: printing the request to select an operation
     * @ param -> return: [] [] -> void
     */
    public void saySelectOperation() {
        System.out.println("Please select an operation:");
    }

    /**
     * @ Method Name: showResult
     * @ Description: printing the result of the operation (UserEntity and the name of the command)
     * @ param -> return: [models.UserEntity, java.lang.String] [userEntity, command] -> void
     */
    public void showResult(UserEntity userEntity, String command) {
        if (userEntity != null) {
            System.out.println("The operation was successful, " + command + "=" + userEntity);
        } else {
            System.out.println("The operation was wrong, could not " + command);
        }
    }

    /**
     * @ Method Name: showResult
     * @ Description: printing the result of the operation (boolean result and the name of the command)
     * @ param -> return: [boolean, java.lang.String] [isSucceed, command] -> void
     */
    public void showResult(boolean isSucceed, String command) {
        if (isSucceed) {
            System.out.println("The operation was successful, " + command);
        } else {
            System.out.println("The operation was wrong, could not " + command);
        }
    }

    /**
     * @ Method Name: showResult
     * @ Description: printing the result of the operation (List<UserEntity>)
     * @ param -> return: [java.util.List<models.UserEntity>] [userEntities] -> void
     */
    public void showResult(List<UserEntity> userEntities) {
        System.out.println("The operation was successful, found " + userEntities.size() + " userEntities:");
        userEntities.forEach(System.out::println);
    }

    /**
     * @ Method Name: askId
     * @ Description: printing the request to enter the Id
     * @ param -> return: [] [] -> void
     */
    public void askId() {
        System.out.println("Write the user's id, to exit press 0");
    }

    /**
     * @ Method Name: askFieldToUpdate
     * @ Description: printing the request to enter the number of field to be updated
     * @ param -> return: [] [] -> void
     */
    public void askFieldToUpdate() {
        System.out.println("If you want "
                + "to change user's name press 1, "
                + "to change user's email press 2, "
                + "to change user's age press 3, "
                + "to exit press 0");
    }
}
