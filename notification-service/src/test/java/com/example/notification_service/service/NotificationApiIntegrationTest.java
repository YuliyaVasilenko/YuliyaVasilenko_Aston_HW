package com.example.notification_service.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 18-04-2026
 * Description: интеграционные тесты для проверки работы NotificationController, NotificationService, EmailService
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationApiIntegrationTest {

    private static GreenMail greenMail;
    private final RestTemplate restTemplate = new RestTemplate();
    @LocalServerPort
    private int port;

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
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> greenMail.getSmtp().getPort());
        registry.add("spring.mail.properties.mail.smtp.auth", () -> "false");
        registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> "false");
    }

    @BeforeEach
    void flush() {
        greenMail.reset();
    }

    @Test
    void send_ShouldSendEmailViaApi() throws Exception {
        String path = "http://localhost:" + port
                + "/api/notifications?email=test@test.com&operation=CREATE";

        ResponseEntity<Void> response = restTemplate.postForEntity(path, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        greenMail.waitForIncomingEmail(100, 1);

        MimeMessage[] msgs = greenMail.getReceivedMessages();

        assertThat(msgs).hasSize(1);
        assertThat(msgs[0].getAllRecipients()[0].toString()).isEqualTo("test@test.com");
        assertThat(msgs[0].getSubject()).isEqualTo("Notification");

        String body = (String) msgs[0].getContent();
        assertThat(body).contains("успешно создан");
    }
}