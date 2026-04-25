package com.example.userservice.cli;

import com.example.userservice.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-03-2026
 * Description: the class that is responsible for outputting information to the console
 */
@Component
public class ConsoleMenuViewer {
    /**
     * @ Method Name: showOperations
     * @ Description: printing all operations which that are in the Menu
     * @ param      : []
     * @ return     : void
     */
    public void showOperations() {
        Arrays.stream(Menu.values())
                .forEach(System.out::println);
    }

    /**
     * @ Method Name: saySelectOperation
     * @ Description: printing the request to select an operation
     * @ param      : []
     * @ return     : void
     */
    public void saySelectOperation() {
        System.out.println("Please select an operation:");
    }

    /**
     * @ Method Name: showResult
     * @ Description: printing the result of the operation (UserDTO and the name of the command)
     * @ param      : [java.util.Optional<com.example.userservice.dto.UserDTO>, java.lang.String]
     * @ return     : void
     */
    public void showResult(Optional<UserDTO> user, String command) {
        if (user.isPresent()) {
            System.out.println("The operation was successful, " + command + "=" + user.get());
        } else {
            System.out.println("The operation was wrong, could not " + command);
        }
    }

    /**
     * @ Method Name: showResult
     * @ Description: printing the result of the operation (List<UserDTO>)
     * @ param      : [java.util.List<com.example.userservice.dto.UserDTO>]
     * @ return     : void
     */
    public void showResult(List<UserDTO> userEntities) {
        System.out.println("The operation was successful, found " + userEntities.size() + " userEntities:");
        userEntities.forEach(System.out::println);
    }

    /**
     * @ Method Name: askId
     * @ Description: printing the request to enter the Id
     * @ param      : []
     * @ return     : void
     */
    public void askId() {
        System.out.println("Write the user's id, to exit press 0");
    }

    /**
     * @ Method Name: askFieldToUpdate
     * @ Description: printing the request to enter the number of field to be updated
     * @ param      : []
     * @ return     : void
     */
    public void askFieldToUpdate() {
        System.out.println("If you want "
                + "to change user's name press 1, "
                + "to change user's email press 2, "
                + "to change user's age press 3, "
                + "to exit press 0");
    }
}

