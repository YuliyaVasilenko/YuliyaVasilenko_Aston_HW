package com.example.user_service.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 10-04-2026
 * Description: Configuration class for the application's Spring context
 */
@EnableAspectJAutoProxy
@Import(com.example.common_models.handler.GlobalExceptionHandler.class)
@Configuration
public class AppConfig {

    /**
     * @ Method Name: modelMapper
     * @ Description: Creates and configures a ModelMapper-bean
     * @ param      : []
     * @ return     : org.modelmapper.ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
