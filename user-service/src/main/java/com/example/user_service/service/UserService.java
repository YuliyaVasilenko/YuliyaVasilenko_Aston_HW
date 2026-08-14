package com.example.user_service.service;

import com.example.common_models.event.UserEvent;
import com.example.common_models.event.UserOperation;
import com.example.common_models.exception.UserNotFoundException;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.kafka.KafkaProducerService;
import com.example.user_service.model.UserEntity;
import com.example.user_service.repository.UserRepository;
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
 * Description: This is a service class for managing user-related business logic
 * and linking it to other components, such as repository for transactional operations and Kafka service
 */
@AllArgsConstructor
@Transactional
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final KafkaProducerService kafkaProducerService;

    /**
     * @ Method Name: createUser
     * @ Description: creates a new user based on the provided UserDTO
     * @ param      : [com.example.user_service.dto.UserDTO]
     * @ return     : com.example.user_service.dto.UserDTO
     */
    public UserDTO createUser(UserDTO userDTO) {
        UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);
        logger.debug("Mapped DTO to entity: {}", userEntity);

        UserEntity savedUser = userRepository.save(userEntity);

        kafkaProducerService.sendMessage(new UserEvent(UserOperation.CREATE, savedUser.getEmail()));

        UserDTO response = modelMapper.map(savedUser, UserDTO.class);
        logger.debug("Mapped entity to DTO: {}", response);

        return response;
    }

    /**
     * @ Method Name: findUserById
     * @ Description: searching for the user by the unique field ID
     * @ param      : [java.lang.Long]
     * @ return     : java.aspects.Optional<com.example.user_service.dto.UserDTO>
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findUserById(Long id) throws UserNotFoundException {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        UserDTO response = modelMapper.map(userEntity, UserDTO.class);
        logger.debug("Mapped entity to DTO: {}", response);

        return Optional.of(response);
    }

    /**
     * @throws UserNotFoundException if user with specified ID does not exist
     * @ Method Name: findAllUsers
     * @ Description: searching for all users
     * @ param      : []
     * @ return     : java.aspects.List<com.example.user_service.dto.UserDTO>
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userEntity -> {
                    UserDTO userDTO = modelMapper.map(userEntity, UserDTO.class);
                    logger.debug("Mapped entity to DTO: {}", userDTO);

                    return userDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * @throws UserNotFoundException if user with specified ID does not exist
     * @ Method Name: updateUser
     * @ Description: updates the user's data
     * @ param      : [java.lang.Long, com.example.user_service.dto.UserDTO]
     * @ return     : com.example.user_service.dto.UserDTO
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) throws UserNotFoundException {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        logger.debug("Updated userEntity fields, old value: Name={}, Email={}, Age={}",
                existingUser.getName(), existingUser.getEmail(), existingUser.getAge());
        if (userDTO.getName() != null) existingUser.setName(userDTO.getName());
        if (userDTO.getEmail() != null) existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getAge() != null && userDTO.getAge() > 0) existingUser.setAge(userDTO.getAge());
        logger.debug("Updated userEntity fields, new value: Name={}, Email={}, Age={}",
                existingUser.getName(), existingUser.getEmail(), existingUser.getAge());

        UserEntity updatedUser = userRepository.save(existingUser);

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
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }

        String email = userRepository.findById(id).map(UserEntity::getEmail).orElseThrow();
        logger.debug("Found user with email: {}", email);

        userRepository.deleteById(id);

        kafkaProducerService.sendMessage(new UserEvent(UserOperation.DELETE, email));
    }

}
