package com.example.notification_service.service;

import com.example.common_models.event.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 17-04-2026
 * Description: the service which is responsible to manage events to another services
 */
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final EmailService emailService;

    /**
     * @ Method Name: processUserEvent
     * @ Description: This is a service class for managing business logic related to notification
     * and linking it to other components, such as email service and Kafka service
     * @ param      : [com.example.common_models.event.UserEvent]
     * @ return     : void
     */
    public void processUserEvent(UserEvent event) {
        emailService.send(event.email(), "Notification", event.operation().getMessage());
    }

}
