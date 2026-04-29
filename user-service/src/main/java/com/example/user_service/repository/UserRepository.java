package com.example.user_service.repository;

import com.example.user_service.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 10-04-2026
 * Description: This is the repository interface for managing UserEntity operations
 * (CRUD and another like findAll) using Spring Data JPA
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
