package com.example.notification_service.service;

import com.example.common_models.event.UserEvent;
import com.example.common_models.event.UserOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 12-04-2026
 * Description: интеграционные тесты для NotificationService и EmailService
 */
@SpringBootTest
public class NotificationServiceTest {

    private final ArgumentCaptor<SimpleMailMessage> messageCaptor
            = ArgumentCaptor.forClass(SimpleMailMessage.class);
    private final String email = "test@example.com";
    private final String subject = "Notification";
    @Autowired
    private NotificationService notificationService;
    @MockitoSpyBean
    private EmailService emailService;
    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        Mockito.reset(mailSender);
    }

    @Test
    void testNotificationOnUserCreation() {
        UserOperation operation = UserOperation.CREATE;
        UserEvent event = new UserEvent(operation, email);

        notificationService.processUserEvent(event);

        verify(emailService, only()).send(email, subject, operation.getMessage());
        verify(mailSender, only()).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();

        assertEquals(email, message.getTo()[0]);
        assertEquals(subject, message.getSubject());
        assertNotNull(message.getText());
        assertTrue(message.getText().contains(operation.getMessage()));
    }

    @Test
    void testNotificationOnUserDeletion() {
        UserOperation operation = UserOperation.DELETE;
        UserEvent event = new UserEvent(operation, email);

        notificationService.processUserEvent(event);

        verify(emailService, only()).send(email, subject, operation.getMessage());
        verify(mailSender, only()).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();

        assertEquals(email, message.getTo()[0]);
        assertEquals(subject, message.getSubject());
        assertNotNull(message.getText());
        assertTrue(message.getText().contains("Ваш аккаунт был удалён"));
    }
}
