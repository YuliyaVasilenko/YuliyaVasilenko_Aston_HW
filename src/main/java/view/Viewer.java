package view;

import menu.Menu;
import models.User;

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
    public static void showOperations() {
        for (Menu menu : Menu.values()) {
            System.out.println(menu);
        }
    }

    /**
     * @ Method Name: saySelectOperation
     * @ Description: printing the request to select an operation
     * @ param -> return: [] [] -> void
     */
    public static void saySelectOperation() {
        System.out.println("Please select an operation:");
    }

    /**
     * @ Method Name: showResult
     * @ Description: printing the result of the operation (User and the name of the command)
     * @ param -> return: [models.User, java.lang.String] [user, command] -> void
     */
    public static void showResult(User user, String command) {
        if (user != null) {
            System.out.println("The operation was successful, " + command + "=" + user);
        } else {
            System.out.println("The operation was wrong, could not " + command);
        }
    }

    /**
     * @ Method Name: showResult
     * @ Description: printing the result of the operation (boolean result and the name of the command)
     * @ param -> return: [boolean, java.lang.String] [isSucceed, command] -> void
     */
    public static void showResult(boolean isSucceed, String command) {
        if (isSucceed) {
            System.out.println("The operation was successful, " + command);
        } else {
            System.out.println("The operation was wrong, could not " + command);
        }
    }

    /**
     * @ Method Name: showResult
     * @ Description: printing the result of the operation (List<User>)
     * @ param -> return: [java.util.List<models.User>] [users] -> void
     */
    public static void showResult(List<User> users) {
        System.out.println("The operation was successful, found " + users.size() + " users:");
        users.forEach(System.out::println);
    }

    /**
     * @ Method Name: askId
     * @ Description: printing the request to enter the Id
     * @ param -> return: [] [] -> void
     */
    public static void askId() {
        System.out.println("Write the user's id, to exit press 0");
    }

    /**
     * @ Method Name: askFieldToUpdate
     * @ Description: printing the request to enter the number of field to be updated
     * @ param -> return: [] [] -> void
     */
    public static void askFieldToUpdate() {
        System.out.println("If you want "
                + "to change user's name press 1, "
                + "to change user's email press 2, "
                + "to change user's age press 3, "
                + "to exit press 0");
    }
}
