package com.example.userservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 10-04-2026
 * Description: Configuration class for the application's Spring context
 */
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
