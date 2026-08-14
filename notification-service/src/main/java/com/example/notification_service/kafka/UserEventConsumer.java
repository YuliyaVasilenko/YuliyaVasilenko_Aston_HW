package com.example.notification_service.kafka;

import com.example.common_models.event.UserEvent;
import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 16-04-2026
 * Description: the consumer which receives events
 */
@RequiredArgsConstructor
@Component
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.user-events-topic}")
    public void consumeUserEvent(UserEvent event) {
        logger.info("Got an event: {}", event);

        notificationService.processUserEvent(event);
    }
}
