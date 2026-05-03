package com.example.notification_service.service;

import com.example.common_models.event.UserOperation;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 18-04-2026
 * Description: integration tests to verify the operation and interaction of
 * NotificationController, NotificationService, EmailService
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationApiIntegrationTest {

    private static GreenMail greenMail;

    private final RestTemplate restTemplate = new RestTemplate();

    @LocalServerPort
    private int port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${app.mail.subject}")
    private String subject;

    @BeforeAll
    static void startMailServer() {
        greenMail = new GreenMail(ServerSetup.SMTP);
        greenMail.start();
    }

    @AfterAll
    static void stopMailServer() {
        greenMail.stop();
    }

    @DynamicPropertySource
    static void mailProps(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.port", () -> greenMail.getSmtp().getPort());
    }

    @BeforeEach
    void flush() {
        greenMail.reset();
    }

    @Test
    void send_ShouldSendEmailViaApi() throws Exception {
        String email = "test@test.com";
        UserOperation operation = UserOperation.CREATE;
        String path = "http://" + mailHost + ":" + port + contextPath +
                "/notifications?email=" + email + "&operation=" + operation;

        ResponseEntity<Void> response = restTemplate.postForEntity(path, null, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        greenMail.waitForIncomingEmail(100, 1);

        MimeMessage[] msgs = greenMail.getReceivedMessages();
        assertEquals(1, msgs.length);

        MimeMessage message = msgs[0];
        assertEquals(email, message.getAllRecipients()[0].toString());
        assertEquals(subject, message.getSubject());

        String body = (String) message.getContent();
        assertTrue(body.contains(operation.getMessage()));
    }
}