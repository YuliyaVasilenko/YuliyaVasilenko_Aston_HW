package dao;

import entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

/**
 * @ Класс с логикой взаимодействия с БД:
 * CRUD-методы+ отображение всей БД
 */
public class UserDao implements EntityDAO {
    private static final Logger logger = LoggerFactory.getLogger(EntityDAO.class);

    /**
     * @ Method Name: create
     * @ Description: создание юзера в БД.
     * Поскольку поле email юзера должно
     * быть уникально, в случае неудачи создания
     * проверяем instanceof исключения: если true-
     * значит email занят, о чем сообщаем логгером
     */
    @Override
    public void create(User user) {
        Transaction transaction = null;
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("Пользователь успешно создан: {}", user);
        } catch (ConstraintViolationException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            // Проверяем причину нарушения ограничения
            Throwable rootCause = e.getCause();
            while (rootCause != null && !(rootCause instanceof SQLIntegrityConstraintViolationException)) {
                rootCause = rootCause.getCause();
            }
            if (rootCause != null) {
                logger.error("Пользователь с email '{}' уже существует", user.getEmail());
                throw new IllegalArgumentException(
                        "Пользователь с email '" + user.getEmail() + "' уже существует", e);
            } else {
                logger.error("Нарушение ограничения целостности данных: ", e);
                throw e;
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при создании пользователя: ", e);
            throw e;
        }
    }

    @Override
    public Optional<User> read(Long id) {
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя по ID: ", e);
            throw e;
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).getResultList();
        } catch (Exception e) {
            logger.error("Ошибка при получении всех пользователей: ", e);
            throw e;
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("Пользователь успешно обновлён: {}", user);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при обновлении пользователя: ", e);
            throw e;
        }
    }

    @Override
    public void remove(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                logger.info("Пользователь с ID {} успешно удалён", id);
            } else {
                logger.warn("Попытка удаления несуществующего пользователя с ID {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при удалении пользователя с ID {}: ", id, e);
            throw e;
        }
    }
}
