package com.example.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 02.05.2026
 * Description: REST controller for user-service circuit breakers
 */
@RestController
public class UserServiceFallbackController {

    /**
     * @ Method Name: userServiceFallback
     * @ Description: the circuit breaker that requests are redirected to when user-service is not available
     * @ param      : []
     * @ return     : org.springframework.http.ResponseEntity<java.lang.String>
     */
    @RequestMapping("/fallback/user/**")
    public ResponseEntity<String> getUserServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User Service is currently unavailable. Please try again later.");
    }

}
