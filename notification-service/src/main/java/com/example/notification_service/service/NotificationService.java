package com.example.notification_service.service;

import com.example.common_models.event.UserEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;

    public void processUserEvent(UserEvent event) {
        logger.info("Processing an event: {}", event);

        emailService.send(event.email(), "Notification", event.operation().getMessage());
    }

}
