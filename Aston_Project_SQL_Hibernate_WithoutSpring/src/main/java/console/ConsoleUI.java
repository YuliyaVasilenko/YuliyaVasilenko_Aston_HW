package console;

import dao.UserDao;
import entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * @ Класс для общения с пользователем:
 * отображает меню, обрабатывает команды.
 * По команде пользователя вызывает соответствующие методы
 * userDAO и отображает результаты их выполнения
 */
public class ConsoleUI {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleUI.class);
    private final UserDao userDao = new UserDao();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean running = true;
        while (running) {
            showMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1 -> createUser();
                case 2 -> readUser();
                case 3 -> showAllUsers();
                case 4 -> updateUser();
                case 5 -> removeUser();
                case 6 -> {
                    running = false;
                    logger.info("Завершение работы приложения");
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
        scanner.close();
    }

    private void showMenu() {
        System.out.println("Выберите действие: ");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Выход");
    }

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void createUser() {
        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Возраст: ");
            Integer age = Integer.parseInt(scanner.nextLine());

            User user = new User(name, email, age);
            userDao.create(user);
            System.out.println("Пользователь успешно создан!");
        } catch (Exception e) {
            System.out.println("Ошибка при создании пользователя: " + e.getMessage());
        }
    }

    private void readUser() {
        try {
            System.out.print("Введите ID пользователя: ");
            Long id = Long.parseLong(scanner.nextLine());
            Optional<User> user = userDao.read(id);
            if (user.isPresent()) {
                System.out.println(user.get());
            } else {
                System.out.println("Пользователь не найден");
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showAllUsers() {
        try {
            List<User> users = userDao.findAll();
            if (users.isEmpty()) {
                System.out.println("Пользователи не найдены");
            } else {
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка пользователей: " + e.getMessage());
        }
    }

    private void updateUser() {
        try {
            System.out.print("Введите ID пользователя для обновления: ");
            Long id = Long.parseLong(scanner.nextLine());
            Optional<User> userOpt = userDao.read(id);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.print("Новое имя (текущее: " + user.getName() + "): ");
                String name = scanner.nextLine();
                if (!name.isEmpty()) user.setName(name);

                System.out.print("Новый email (текущий: " + user.getEmail() + "): ");
                String email = scanner.nextLine();
                if (!email.isEmpty()) user.setEmail(email);

                System.out.print("Новый возраст (текущий: " + user.getAge() + "): ");
                String ageStr = scanner.nextLine();
                if (!ageStr.isEmpty()) user.setAge(Integer.parseInt(ageStr));

                userDao.update(user);
                System.out.println("Пользователь успешно обновлён!");
            } else {
                System.out.println("Пользователь не найден");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении пользователя: " + e.getMessage());
        }
    }

    private void removeUser() {
        try {
            System.out.print("Введите ID пользователя для удаления: ");
            Long id = Long.parseLong(scanner.nextLine());
            userDao.remove(id);
            System.out.println("Пользователь удалён!");
        } catch (Exception e) {
            System.out.println("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }
}
