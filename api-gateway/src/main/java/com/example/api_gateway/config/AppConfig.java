package com.example.api_gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 02.05.2026
 * Description: Configuration class for the application's Spring context
 */
@Import(com.example.common_models.handler.GlobalExceptionHandler.class)
@Configuration
public class AppConfig {
}
