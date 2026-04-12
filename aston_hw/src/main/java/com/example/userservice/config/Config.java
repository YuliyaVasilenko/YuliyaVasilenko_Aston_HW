package com.example.userservice.config;

import com.example.userservice.console.Console;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public CommandLineRunner commandLineRunner(Console userConsole) {
        return args -> userConsole.start();
    }

}
