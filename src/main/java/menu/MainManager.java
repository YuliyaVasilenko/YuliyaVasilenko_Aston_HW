package menu;

import dao.UserDAO;
import input.CheckWriting;
import models.User;
import view.Viewer;

import java.util.List;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 27-03-2026
 * Description: this class describes the basic logic of application -
 * interaction between input (CheckWriting), output (Viewer) and database (UserDAO)
 */
public class MainManager {
    private UserDAO userDAO = new UserDAO();
    private CheckWriting checker = new CheckWriting();

    /**
     * @ Method Name: find
     * @ Description: select the 'id' and send a request to search in the database
     * @ param -> return: [] [] -> models.User
     */
    public User find() {
        int id = selectId();
        return userDAO.findById(id);
    }

    /**
     * @ Method Name: create
     * @ Description: ask to enter the fields for the user's object (name, email, age),
     * then create a user and send a request to create an entry in the database
     * @ param -> return: [] [] -> models.User
     */
    public User create() {
        String name = checker.checkWord("name");
        if (name != null) {
            String email = checker.checkEmail();
            if (email != null) {
                int age = checker.checkNumber("age", 1, 100);
                if (age > 0) {
                    User user = new User(name, email, age);
                    return userDAO.create(user);
                }
            }
        }
        return null;
    }

    /**
     * @ Method Name: findAll
     * @ Description: send a request to search all users in the database
     * @ param -> return: [] [] -> java.util.List<models.User>
     */
    public List<User> findAll() {
        return userDAO.findAll();
    }

    /**
     * @ Method Name: update
     * @ Description: find the user, then ask which field should to be updated and the new data,
     * then update the selected field, then send a request to update the user in the database
     * @ param -> return: [] [] -> models.User
     */
    public User update() {
        User user = find();
        if (user != null) {
            Viewer.askFieldToUpdate();
            int numberOfFieldToUpdate = checker.checkNumber("field to update", 1, 3);
            if (numberOfFieldToUpdate == 0) {
                user = null;
            } else {
                switch (numberOfFieldToUpdate) {
                    case 1 -> {
                        String name = checker.checkWord("name");
                        user.setName(name);
                    }
                    case 2 -> {
                        String email = checker.checkWord("email");
                        user.setEmail(email);
                    }
                    case 3 -> {
                        int age = checker.checkNumber("age", 1, 100);
                        user.setAge(age);
                    }
                }
                user = userDAO.update(user);
            }
        }
        return user;
    }

    /**
     * @ Method Name: delete
     * @ Description: select the 'id' and send a request to search in the database
     * @ param -> return: [] [] -> boolean
     */
    public boolean delete() {
        int id = selectId();
        return userDAO.delete(id);
    }

    /**
     * @ Method Name: selectId
     * @ Description: ask to enter the 'id' and check received number
     * @ param -> return: [] [] -> int
     */
    public int selectId() {
        Viewer.askId();
        return checker.checkNumber("id", 1, 1000);
    }
}
