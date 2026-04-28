package com.example.common_models.exception;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 11-04-2026
 * Description: This class provides information about data validation errors
 */
@Getter
@ToString
public class ValidationError {

    private final int statusCode;

    private final String message;

    private final LocalDateTime timestamp;

    public ValidationError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        timestamp = LocalDateTime.now();
    }

}
