package dao;

import models.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HibernateUtil;

import java.util.List;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-03-2026
 * Description: this class describes the interaction between a user's entity and a database
 */
public class UserDAO implements EntityDAO<User> {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    /**
     * @ Method Name: findById
     * @ Description: searching for the user in the database by the unique field 'id'
     * @ param -> return: [int] [id] -> models.User
     */
    @Override
    public User findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (id < 0) {
                logger.warn("Attempt to find user with ID <= 0, ID: {}", id);
            }
            else {
                User user = session.get(User.class, id);
                if (user != null) {
                    logger.info("User found with ID: {}", id);
                    return user;
                } else {
                    logger.warn("User not found with ID: {}", id);
                }
            }
        } catch (Exception e) {
            logger.error("Error finding user by ID {}: {}", id, e.getMessage());
        }
        return null;
    }

    /**
     * @ Method Name: findAll
     * @ Description: searching for all users in the database
     * @ param -> return: [] [] -> java.util.List<models.User>
     */
    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).getResultList();
            logger.info("Users found: {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error finding users: {}", e.getMessage());
        }
        return null;
    }

    /**
     * @ Method Name: create
     * @ Description: creating a new user in the database
     * @ param -> return: [models.User] [user] -> models.User
     */
    @Override
    public User create(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User saved successfully with ID: {}", user.getId());
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving user: {}", e.getMessage());
        }
        return null;
    }

    /**
     * @ Method Name: update
     * @ Description: updating the user data in the database
     * @ param -> return: [models.User] [user] -> models.User
     */
    @Override
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("User updated successfully with ID: {}", user.getId());
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating user: {}", e.getMessage());
        }
        return null;
    }

    /**
     * @ Method Name: delete
     * @ Description: deleting the user from the database
     * @ param -> return: [models.User] [user] -> boolean
     */
    @Override
    public boolean delete(int id) {
        User user = null;
        if (id <= 0) {
            logger.warn("Attempt to delete user with ID <= 0, ID: {}", id);
        } else {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                user = session.get(User.class, id);
                if (user == null) {
                    logger.warn("Attempt to delete non-existent user");
                }
                else {
                    session.remove(user);
                    transaction.commit();
                    logger.info("User deleted successfully with ID: {}", user.getId());
                    return true;
                }
            } catch (Exception e) {
                if (transaction != null && user != null) {
                    transaction.rollback();
                }
                logger.error("Error deleting user: {}", e.getMessage());
            }
        }
        return false;
    }
}
