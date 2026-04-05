package dao;

import models.UserEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-03-2026
 * Description: this class describes the interaction between a user's entity and a database
 */
public class UserDAO implements EntityDAO<UserEntity> {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    /**
     * @ Method Name: findById
     * @ Description: searching for the user in the database by the unique field 'id'
     * @ param -> return: [int] [id] -> models.UserEntity
     */
    @Override
    public UserEntity findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (id <= 0) {
                logger.warn("Attempt to find user with ID <= 0, ID: {}", id);
            } else {
                UserEntity userEntity = session.get(UserEntity.class, id);
                if (userEntity != null) {
                    logger.info("UserEntity found with ID: {}", id);
                    return userEntity;
                } else {
                    logger.warn("UserEntity not found with ID: {}", id);
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
     * @ param -> return: [] [] -> java.util.List<models.UserEntity>
     */
    @Override
    public List<UserEntity> findAll() {
        List<UserEntity> userEntities = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            userEntities = session.createQuery("FROM UserEntity", UserEntity.class).getResultList();
            logger.info("Users found: {} userEntities", userEntities.size());
            return userEntities;
        } catch (Exception e) {
            logger.error("Error finding userEntities: {}", e.getMessage());
        }
        return userEntities;
    }

    /**
     * @ Method Name: create
     * @ Description: creating a new userEntity in the database
     * @ param -> return: [models.UserEntity] [userEntity] -> models.UserEntity
     */
    @Override
    public UserEntity create(UserEntity userEntity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(userEntity);
            transaction.commit();
            logger.info("UserEntity saved successfully with ID: {}", userEntity.getId());
            return userEntity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving userEntity: {}", e.getMessage());
        }
        return null;
    }

    /**
     * @ Method Name: update
     * @ Description: updating the userEntity data in the database
     * @ param -> return: [models.UserEntity] [userEntity] -> models.UserEntity
     */
    @Override
    public UserEntity update(UserEntity userEntity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(userEntity);
            transaction.commit();
            logger.info("UserEntity updated successfully with ID: {}", userEntity.getId());
            return userEntity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating userEntity: {}", e.getMessage());
        }
        return null;
    }

    /**
     * @ Method Name: delete
     * @ Description: deleting the user from the database
     * @ param -> return: [models.UserEntity] [user] -> boolean
     */
    @Override
    public boolean delete(int id) {
        UserEntity userEntity = null;
        if (id <= 0) {
            logger.warn("Attempt to delete userEntity with ID <= 0, ID: {}", id);
        } else {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                userEntity = session.get(UserEntity.class, id);
                if (userEntity == null) {
                    logger.warn("Attempt to delete non-existent userEntity");
                } else {
                    session.remove(userEntity);
                    transaction.commit();
                    logger.info("UserEntity deleted successfully with ID: {}", userEntity.getId());
                    return true;
                }
            } catch (Exception e) {
                if (transaction != null && userEntity != null) {
                    transaction.rollback();
                }
                logger.error("Error deleting userEntity: {}", e.getMessage());
            }
        }
        return false;
    }
}
