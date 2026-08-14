package com.example.user_service.service;

import com.example.common_models.event.UserEvent;
import com.example.common_models.event.UserOperation;
import com.example.common_models.exception.UserNotFoundException;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.kafka.KafkaProducerService;
import com.example.user_service.model.UserEntity;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

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

        UserEntity savedUser = userRepository.save(userEntity);

        kafkaProducerService.sendMessage(new UserEvent(UserOperation.CREATE, savedUser.getEmail()));

        return modelMapper.map(savedUser, UserDTO.class);
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
                .map(userEntity -> modelMapper.map(userEntity, UserDTO.class))
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

        if (userDTO.getName() != null) existingUser.setName(userDTO.getName());
        if (userDTO.getEmail() != null) existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getAge() != null && userDTO.getAge() > 0) existingUser.setAge(userDTO.getAge());

        UserEntity updatedUser = userRepository.save(existingUser);

        return modelMapper.map(updatedUser, UserDTO.class);
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

        userRepository.deleteById(id);

        kafkaProducerService.sendMessage(new UserEvent(UserOperation.DELETE, email));
    }

}
