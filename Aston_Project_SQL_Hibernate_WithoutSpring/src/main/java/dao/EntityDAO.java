package dao;

import entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @ Абстракция с CRUD-операциями
 * и отображением всей БД.
 */
public interface EntityDAO {
    void create(User user);

    Optional<User> read(Long id);

    void update(User user);

    void remove(Long id);

    List<User> findAll();
}
