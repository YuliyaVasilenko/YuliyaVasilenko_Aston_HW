package com.example.userservice.cli;

import com.example.userservice.cli.validator.ConsoleValidator;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 27-03-2026
 * Description: this class describes how the application console menu works
 */
@Component
@AllArgsConstructor
@Profile("!test")
public class ConsoleMenu implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleMenu.class);

    private UserService userService;

    private ConsoleValidator consoleValidator;

    private ConsoleMenuViewer viewer;

    @Override
    public void run(String... args) {
        proceed();
    }

    /**
     * @ Method Name: proceed
     * @ Description: the main method of operation of the menu, the general order of application processing
     * @ param      : []
     * @ return     : void
     */
    public void proceed() {
        logger.info("The console menu has started");
        Menu menu;

        do {
            viewer.saySelectOperation();
            viewer.showOperations();

            menu = selectOperation();
            logger.info("The operation was selected: {}", menu.name());

            if (menu != Menu.EXIT) {
                switchOperation(menu);
                logger.info("The operation was proceed: {}", menu.name());
            }

        } while (menu != Menu.EXIT);

        logger.info("The exit from the program was selected");
    }

    /**
     * @ Method Name: switchOperation
     * @ Description: describes which procedure is called for each 'button'
     * @ param      : [com.example.userservice.cli.Menu, com.example.userservice.service.UserService]
     * @ return     : void
     */
    public void switchOperation(Menu menu) {
        switch (menu) {
            case CREATE -> create();
            case READ -> read();
            case READ_ALL -> readAll();
            case UPDATE -> update();
            case DELETE -> delete();
        }
    }

    /**
     * @ Method Name: selectOperation
     * @ Description: asks which 'button' is selected; connects the selection in console (int number) and the Menu value
     * @ param      : []
     * @ return     : com.example.userservice.cli.Menu
     */
    public Menu selectOperation() {
        int ordinal = consoleValidator.checkNumber("operation",
                1, Menu.values().length) - 1;

        if (ordinal == -1) ordinal = Menu.EXIT.ordinal();

        return Menu.getOrdinal(ordinal);
    }

    /**
     * @ Method Name: selectId
     * @ Description: ask to enter the ID and check received number
     * @ param      : []
     * @ return     : java.lang.Long
     */
    public Long selectId() {
        viewer.askId();

        return (long) consoleValidator.checkNumber("id", 1, 1000);
    }

    /**
     * @ Method Name: create
     * @ Description: ask to enter the fields for the user's object (name, email, age),
     * then create a user and send a request to create an entry in the database
     * @ param      : []
     * @ return     : void
     */
    public void create() {
        Optional<UserDTO> userCreated = Optional.empty();
        String name = consoleValidator.checkWord("name");
        if (name != null) {
            String email = consoleValidator.checkEmail();
            if (email != null) {
                int age = consoleValidator.checkNumber("age", 1, 100);
                if (age > 0) {
                    UserDTO user = new UserDTO(name, email, age);

                    userCreated = Optional.ofNullable(userService.createUser(user));
                }
            }
        }
        viewer.showResult(userCreated, Menu.CREATE.getDescription());
    }

    /**
     * @ Method Name: read
     * @ Description: selects the ID and send a request to search in the database
     * @ param      : []
     * @ return     : void
     */
    public void read() {
        Long id = selectId();

        Optional<UserDTO> user = Optional.empty();

        try {
            user = userService.findUserById(id);
        } catch (UserNotFoundException exception) {
            System.out.println(exception.getMessage());
        }

        viewer.showResult(user, Menu.READ.getDescription());
    }

    /**
     * @ Method Name: readAll
     * @ Description: sends a request to search for all users in the database
     * @ param      : []
     * @ return     : void
     */
    public void readAll() {
        List<UserDTO> users = userService.findAllUsers();

        viewer.showResult(users);
    }

    /**
     * @ Method Name: update
     * @ Description: asks the user's ID, then asks which field should to be updated and the new data,
     * then update the selected field, then send a request to update the user in the database
     * @ param      : []
     * @ return     : void
     */
    public void update() {
        long id = selectId();
        Optional<UserDTO> user = Optional.empty();
        UserDTO userDTO = new UserDTO();

        viewer.askFieldToUpdate();
        int numberOfFieldToUpdate = consoleValidator.checkNumber("field to update", 1, 3);

        if (numberOfFieldToUpdate != 0) {
            switch (numberOfFieldToUpdate) {
                case 1 -> {
                    String name = consoleValidator.checkWord("name");
                    userDTO.setName(name);
                }
                case 2 -> {
                    String email = consoleValidator.checkEmail();
                    userDTO.setEmail(email);
                }
                case 3 -> {
                    int age = consoleValidator.checkNumber("age", 1, 100);
                    userDTO.setAge(age);
                }
            }

            if (userDTO.getName() != null || userDTO.getEmail() != null ||
                    (userDTO.getAge() != null && userDTO.getAge() > 0)) {
                try {
                    UserDTO updatedUserDTO = userService.updateUser(id, userDTO);
                    user = Optional.ofNullable(updatedUserDTO);
                } catch (UserNotFoundException exception) {
                    System.out.println(exception.getMessage());
                }
            }
        }

        viewer.showResult(user, Menu.UPDATE.getDescription());
    }

    /**
     * @ Method Name: delete
     * @ Description: selects the ID and send a request to delete from the database
     * @ param      : []
     * @ return     : void
     */
    public void delete() {
        Long id = selectId();

        try {
            userService.deleteUser(id);
        } catch (UserNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
