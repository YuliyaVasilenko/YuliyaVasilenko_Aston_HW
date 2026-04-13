package com.example.userservice.repository;

import com.example.userservice.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 10-04-2026
 * Description: This is the repository interface for managing UserEntity operations (CRUD and another like findAll) using Spring Data JPA
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
