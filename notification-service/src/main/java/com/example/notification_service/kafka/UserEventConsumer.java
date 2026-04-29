package com.example.notification_service.kafka;

import com.example.common_models.event.UserEvent;
import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
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

    private final NotificationService notificationService;

    /**
     * @ Method Name: consumeUserEvent
     * @ Description: consumes an event fron kafka and provides it to the management of the service
     * @ param      : [com.example.common_models.event.UserEvent]
     * @ return     : void
     */
    @KafkaListener(topics = "${app.kafka.user-events-topic}")
    public void consumeUserEvent(UserEvent event) {
        notificationService.processUserEvent(event);
    }
}
