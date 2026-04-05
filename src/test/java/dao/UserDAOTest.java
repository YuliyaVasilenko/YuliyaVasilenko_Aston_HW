package dao;

import models.UserEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import utils.HibernateUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 04-04-2026
 * Description: тесты для класса UserDAO
 */
@Testcontainers
class UserDAOTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @AfterEach
    void tearDown() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM UserEntity").executeUpdate();
            tx.commit();
        }
    }

    @Test
    void create_ShouldCreateUser_ReturnUser() {
        String name = "Tom";
        String email = "tom@gmail.com";
        int age = 20;
        UserEntity userEntity = new UserEntity(name, email, age);
        UserEntity createdUserEntity = new UserEntity(name, email, age);

        createdUserEntity = userDAO.create(createdUserEntity);

        assertEquals(0, userEntity.getId());
        assertNull(userEntity.getCreated_at());
        assertNull(userEntity.getUpdated_at());

        assertNotNull(createdUserEntity);
        assertTrue(createdUserEntity.getId() > 0);
        assertEquals(name, createdUserEntity.getName());
        assertEquals(email, createdUserEntity.getEmail());
        assertEquals(age, createdUserEntity.getAge());
        assertNotNull(createdUserEntity.getCreated_at());
        assertNotNull(createdUserEntity.getUpdated_at());
    }

    @Test
    void findById_ValidId_ReturnUser() {
        UserEntity userEntity = new UserEntity("Mary", "mary@yandex.ru", 25);
        UserEntity createdUserEntity = userDAO.create(userEntity);

        UserEntity foundUserEntity = userDAO.findById(createdUserEntity.getId());

        assertNotNull(foundUserEntity);
        assertEquals(createdUserEntity, foundUserEntity);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10, -1000})
    void findById_NegativeOrZeroId_ReturnNull(int invalidId) {
        UserEntity foundUserEntity;

        foundUserEntity = userDAO.findById(invalidId);

        assertNull(foundUserEntity);
    }

    @Test
    void findById_UserNotFoundCaseIdNotExists_ReturnNull() {
        int nonExistentId = 999;

        UserEntity foundUserEntity = userDAO.findById(nonExistentId);

        assertNull(foundUserEntity);
    }

    @Test
    void findAll_shouldFindAllUsers_ReturnListOfUsers() {
        UserEntity userEntity1 = new UserEntity("John", "john@example.com", 26);
        UserEntity userEntity2 = new UserEntity("Jane", "jane@example.com", 34);
        List<UserEntity> expected = List.of(userEntity1, userEntity2);
        userEntity1 = userDAO.create(userEntity1);
        int id1 = userEntity1.getId();
        userEntity2 = userDAO.create(userEntity2);
        int id2 = userEntity2.getId();

        List<UserEntity> result = userDAO.findAll();

        assertEquals(2, result.size());
        assertEquals(expected, result);

        boolean hasUser1 = result.stream()
                .anyMatch(u -> u.getId() == id1 && "John".equals(u.getName()));
        boolean hasUser2 = result.stream()
                .anyMatch(u -> u.getId() == id2 && "Jane".equals(u.getName()));
        assertTrue(hasUser1);
        assertTrue(hasUser2);
    }

    @Test
    void update_shouldUpdateUser() {
        UserEntity userEntity = new UserEntity("OldName", "old@example.com", 85);
        userDAO.create(userEntity);

        userEntity.setName("NewName");
        userEntity.setEmail("new@example.com");
        userEntity.setAge(75);

        UserEntity updatedUserEntity = userDAO.update(userEntity);

        assertNotNull(updatedUserEntity);
        assertEquals(userEntity, updatedUserEntity);
        assertEquals("NewName", updatedUserEntity.getName());
        assertEquals("new@example.com", updatedUserEntity.getEmail());
        assertEquals(75, updatedUserEntity.getAge());
    }

    @Test
    void delete_ValidId_ReturnTrue() {
        UserEntity userEntity = new UserEntity("ToBeDeleted", "delete@example.com", 53);
        userEntity = userDAO.create(userEntity);
        int id = userEntity.getId();

        boolean isDeleted = userDAO.delete(id);
        UserEntity foundUserEntity = userDAO.findById(id);

        assertTrue(isDeleted);
        assertNull(foundUserEntity);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10, -1000})
    void delete_NegativeOrZeroId_ReturnFalse(int invalidId) {
        boolean result;

        result = userDAO.delete(invalidId);

        assertFalse(result);
    }

    @Test
    void delete_UserNotFoundCaseIdNotExists_ReturnFalse() {
        int nonExistentId = 999;

        boolean result = userDAO.delete(nonExistentId);

        assertFalse(result);
    }
}