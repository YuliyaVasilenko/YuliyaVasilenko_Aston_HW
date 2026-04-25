package com.example.notification_service.controller;

import com.example.common_models.event.UserOperation;
import com.example.notification_service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 20-04-2026
 * Description: тесты для класса NotificationController
 */
@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    private String path;

    @BeforeEach
    void setUp() {
        path = "/api/notifications";
    }

    @Test
    void sendNotification_ShouldReturnOK() throws Exception {
        String testEmail = "test@test.com";

        mockMvc.perform(post(path)
                        .param("email", testEmail)
                        .param("operation", "CREATE"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).processUserEvent(
                argThat(event -> UserOperation.CREATE.equals(event.operation()) &&
                        testEmail.equals(event.email()))
        );
    }

    @Test
    void sendNotification_NotAllParameters_ReturnBadRequest() throws Exception {
        String testEmail = "notEmail";

        mockMvc.perform(post(path)
                        .param("email", testEmail))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendNotification_ValidationError_ReturnBadRequest() throws Exception {
        String testEmail = "notEmail";

        mockMvc.perform(post(path)
                        .param("email", testEmail)
                        .param("operation", "UNKNOWN"))
                .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public UserOperationConverter userOperationConverter() {
            return new UserOperationConverter();
        }

        @Bean
        public WebMvcConfigurer webMvcConfigurer(UserOperationConverter converter) {
            return new WebMvcConfigurer() {
                @Override
                public void addFormatters(FormatterRegistry registry) {
                    registry.addConverter(converter);
                }
            };
        }
    }

    static class UserOperationConverter implements Converter<String, UserOperation> {
        @Override
        public UserOperation convert(String source) {
            try {
                return UserOperation.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UserOperation: " + source);
            }
        }
    }
}