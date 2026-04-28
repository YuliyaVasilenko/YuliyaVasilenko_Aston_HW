package com.example.notification_service.controller;


import com.example.common_models.event.UserEvent;
import com.example.common_models.event.UserOperation;
import com.example.notification_service.service.NotificationService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 17-04-2026
 * Description: the class which describes REST API for sending an email
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
@Validated
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    @PostMapping()
    public ResponseEntity<Void> sendNotification(
            @RequestParam
            @Email(message = "Incorrect email format")
            @NotBlank(message = "Email should not be blank")
            String email,

            @RequestParam
            @NotNull(message = "Operation should not be blank")
            UserOperation operation) {

        logger.info("Received a request to send a notification. Request email: {}, request operation: {}", email, operation);

        notificationService.processUserEvent(new UserEvent(operation, email));

        return ResponseEntity.ok().build();
    }
}