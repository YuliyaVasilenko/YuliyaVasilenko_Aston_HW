package dao;

import java.util.List;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-03-2026
 * Description: this interface provides methods that are required for the interaction between some entity and a database
 */
public interface EntityDAO<T> {
    /**
     * @ Method Name: create
     * @ Description: creating a new entity in the database
     * @ param -> return: [T] [object] -> T
     */
    T create(T object);

    /**
     * @ Method Name: findById
     * @ Description: searching for the entity in the database by the unique field 'id'
     * @ param -> return: [int] [id] -> T
     */
    T findById(int id);

    /**
     * @ Method Name: findAll
     * @ Description: searching for all entities in the database
     * @ param -> return: [] [] -> java.util.List<T>
     */
    List<T> findAll();

    /**
     * @ Method Name: update
     * @ Description: updating the entity data in the database
     * @ param -> return: [T] [object] -> T
     */
    T update(T object);

    /**
     * @ Method Name: delete
     * @ Description: deleting the entity from the database
     * @ param -> return: [T] [object] -> boolean
     */
    boolean delete(int id);
}
