package com.example.user_service.service;

import com.example.common_models.event.UserEvent;
import com.example.common_models.event.UserOperation;
import com.example.user_service.BaseIntegrationTest;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.kafka.KafkaProducerService;
import com.example.user_service.model.UserEntity;
import com.example.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 19-04-2026
 * Description: интеграционные тесты для класса UserService (взаимодействие с Kafka)
 */
public class UserServiceKafkaTest extends BaseIntegrationTest {

    private final String TEST_EMAIL = "test@email.com";

    @Autowired
    private UserService userService;

    @MockitoSpyBean
    private KafkaProducerService kafkaProducerService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private KafkaConsumer consumer;

    private UserEvent expectedEvent;
    private UserDTO userDTO;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO("Test Name", TEST_EMAIL, 33);
        userEntity = new UserEntity();
        userEntity.setEmail(TEST_EMAIL);
        userEntity.setId(500L);

        consumer.reset();
    }

    @Test
    void createUser_ShouldSendCreationKafkaEvent() throws Exception {
        when(userRepository.save(any())).thenReturn(userEntity);
        expectedEvent = new UserEvent(UserOperation.CREATE, TEST_EMAIL);

        userService.createUser(userDTO);

        assertTrue(consumer.getLatch().await(10, TimeUnit.SECONDS));
        verify(kafkaProducerService, times(1)).sendMessage(expectedEvent);
        assertNotNull(consumer.getReceivedEvent());
        assertEquals(UserOperation.CREATE, consumer.getReceivedEvent().operation());
        assertEquals(TEST_EMAIL, consumer.getReceivedEvent().email());
    }

    @Test
    void deleteUser_ShouldSendDeletionKafkaEvent() throws Exception {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).deleteById(anyLong());
        expectedEvent = new UserEvent(UserOperation.DELETE, TEST_EMAIL);

        userService.deleteUser(100L);

        assertTrue(consumer.getLatch().await(10, TimeUnit.SECONDS));
        verify(kafkaProducerService, times(1)).sendMessage(expectedEvent);
        assertNotNull(consumer.getReceivedEvent());
        assertEquals(UserOperation.DELETE, consumer.getReceivedEvent().operation());
        assertEquals(TEST_EMAIL, consumer.getReceivedEvent().email());
    }
}
