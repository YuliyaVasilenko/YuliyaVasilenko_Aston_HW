package com.example.notification_service.service;


import com.example.common_models.event.UserEvent;
import com.example.common_models.event.UserOperation;
import com.example.notification_service.BaseIntegrationTest;
import com.example.notification_service.kafka.UserEventConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 24-04-2026
 * Description: integration tests for the NotificationService class (interaction with Kafka)
 */
@ActiveProfiles("test")
public class NotificationServiceKafkaTest extends BaseIntegrationTest {

    @Autowired
    KafkaTemplate<String, UserEvent> kafkaTemplate;

    @MockitoSpyBean
    private UserEventConsumer consumer;

    @MockitoBean
    private NotificationService notificationService;

    @Value("${app.kafka.user-events-topic}")
    private String topicName;

    @Test
    void consumeUserEvent_WhenMessageReceived_ShouldProcessEvent() {
        UserEvent event = new UserEvent(UserOperation.CREATE, "test@email.com");
        System.out.println("TOPIC=" + topicName);

        kafkaTemplate.send(topicName, event);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                    verify(consumer, times(1)).consumeUserEvent(event);
                    verify(notificationService, times(1)).processUserEvent(event);
                }
        );
    }
}
