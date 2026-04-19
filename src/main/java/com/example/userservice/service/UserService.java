package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.kafka.KafkaProducerService;
import com.example.userservice.kafka.UserEvent;
import com.example.userservice.kafka.UserOperation;
import com.example.userservice.model.UserEntity;
import com.example.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 10-04-2026
 * Description: This is service class for managing user-related business logic with transactional operations
 */
@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final KafkaProducerService kafkaProducerService;

    /**
     * @ Method Name: createUser
     * @ Description: creates a new user based on the provided UserDTO
     * @ param      : [com.example.userservice.dto.UserDTO]
     * @ return     : com.example.userservice.dto.UserDTO
     */
    public UserDTO createUser(UserDTO userDTO) {
        logger.info("Starting user creation. Request data: {}", userDTO);

        UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);
        logger.debug("Mapped DTO to entity: {}", userEntity);

        UserEntity savedUser = userRepository.save(userEntity);
        logger.info("User saved to database with ID: {}", savedUser.getId());

        kafkaProducerService.sendMessage(new UserEvent(UserOperation.CREATE, savedUser.getEmail()));
        logger.info("Sent to Kafka an event: {}, to email: {}", UserOperation.CREATE, savedUser.getEmail());

        UserDTO response = modelMapper.map(savedUser, UserDTO.class);
        logger.debug("Mapped entity to response DTO: {}", response);

        return response;
    }

    /**
     * @ Method Name: findUserById
     * @ Description: searching for the user by the unique field ID
     * @ param      : [java.lang.Long]
     * @ return     : java.util.Optional<com.example.userservice.dto.UserDTO>
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findUserById(Long id) throws UserNotFoundException {
        logger.info("Fetching user by ID: {}", id);

        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> {
            logger.warn("User not found with ID: {}", id);
            return new UserNotFoundException("User not found");
        });

        logger.info("User found: {}", userEntity);

        UserDTO response = modelMapper.map(userEntity, UserDTO.class);
        logger.debug("Successfully mapped user entity to DTO for ID: {}", id);

        return Optional.of(response);
    }

    /**
     * @throws UserNotFoundException if user with specified ID does not exist
     * @ Method Name: findAllUsers
     * @ Description: searching for all users
     * @ param      : []
     * @ return     : java.util.List<com.example.userservice.dto.UserDTO>
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        logger.info("Fetching all users from database");

        List<UserDTO> users = userRepository.findAll()
                .stream()
                .map(userEntity -> modelMapper.map(userEntity, UserDTO.class))
                .collect(Collectors.toList());

        logger.info("Successfully fetched {} users from database", users.size());
        return users;
    }

    /**
     * @throws UserNotFoundException if user with specified ID does not exist
     * @ Method Name: updateUser
     * @ Description: updates the user's data
     * @ param      : [java.lang.Long, com.example.userservice.dto.UserDTO]
     * @ return     : com.example.userservice.dto.UserDTO
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) throws UserNotFoundException {
        logger.info("Starting user update for ID: {}. Update data: {}", id, userDTO);

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Attempt to update non-existent user with ID: {}", id);
                    return new UserNotFoundException("User not found");
                });

        logger.debug("Updated userEntity fields, old value: Name={}, Email={}, Age={}",
                existingUser.getName(), existingUser.getEmail(), existingUser.getAge());
        if (userDTO.getName() != null) existingUser.setName(userDTO.getName());
        if (userDTO.getEmail() != null) existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getAge() != null && userDTO.getAge() > 0) existingUser.setAge(userDTO.getAge());
        logger.debug("Updated userEntity fields, new value: Name={}, Email={}, Age={}",
                existingUser.getName(), existingUser.getEmail(), existingUser.getAge());

        UserEntity updatedUser = userRepository.save(existingUser);
        logger.info("User updated successfully. ID: {}", updatedUser.getId());

        UserDTO response = modelMapper.map(updatedUser, UserDTO.class);
        logger.debug("Mapped updated entity to response DTO: {}", response);
        return response;
    }

    /**
     * @throws UserNotFoundException if user with specified ID does not exist
     * @ Method Name: deleteUser
     * @ Description: deletes the user by the unique field ID
     * @ param      : [java.lang.Long]
     * @ return     : void
     */
    public void deleteUser(Long id) throws UserNotFoundException {
        logger.info("Starting user deletion for ID: {}", id);

        if (!userRepository.existsById(id)) {
            logger.warn("Attempt to delete non-existent user with ID: {}", id);
            throw new UserNotFoundException("User to delete not found");
        }

        String email = userRepository.findById(id).map(UserEntity::getEmail).orElseThrow();
        userRepository.deleteById(id);

        kafkaProducerService.sendMessage(new UserEvent(UserOperation.DELETE, email));
        System.out.println("kafka");
        logger.info("User successfully deleted with ID: {}", id);
    }

}
