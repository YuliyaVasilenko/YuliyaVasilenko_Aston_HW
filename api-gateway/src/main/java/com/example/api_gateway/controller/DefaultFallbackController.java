package com.example.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 01.05.2026
 * Description: REST controller for default circuit breakers
 */
@RestController
@RequestMapping("/fallback/default")
public class DefaultFallbackController {

    /**
     * @ Method Name: defaultFallback
     * @ Description: the default circuit breaker that requests are redirected to
     * @ param      : []
     * @ return     : org.springframework.http.ResponseEntity<java.lang.String>
     */
    @PostMapping
    public ResponseEntity<String> defaultFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Service is currently unavailable. Please try again later.");
    }

}
