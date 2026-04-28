package com.example.common_models.handler;

import com.example.common_models.exception.UserNotFoundException;
import com.example.common_models.exception.ValidationError;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 10-04-2026
 * Description: Global exception handler for the application that centralizes error handling across the controller
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * @ Method Name: handleConstraintViolation
     * @ Description: handles ConstraintViolationException exceptions thrown during bean validation
     * (e.g., in @PathVariable @NotNull parameters)
     * @ param      : [jakarta.validation.ConstraintViolationException]
     * @ return     : org.springframework.http.ResponseEntity<java.util.List<com.example.user_service.exception.ValidationError>>;
     * ResponseEntity with list of ValidationError and HTTP status 400 (BAD_REQUEST)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ValidationError>> handleConstraintViolation(ConstraintViolationException exception) {
        logger.warn("Validation failed: ConstraintViolationException caught. Total violations: {}",
                exception.getConstraintViolations().size());

        List<ValidationError> validationErrors = exception.getConstraintViolations()
                .stream()
                .map(violation -> {
                    logger.info("Validation violation - Path: {}, Message: {}, Invalid value: {}",
                            violation.getPropertyPath(), violation.getMessage(), violation.getInvalidValue());

                    return new ValidationError(HttpStatus.BAD_REQUEST.value(), violation.getMessage());
                })
                .toList();

        logger.warn("Request rejected due to validation errors. Details: {}", validationErrors);
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    /**
     * @ Method Name: handleValidationExceptions
     * @ Description: handles MethodArgumentNotValidException exceptions thrown when method arguments fail validation
     * (e.g., in @RequestBody parameters)
     * @ param      : [org.springframework.web.bind.MethodArgumentNotValidException]
     * @ return     : org.springframework.http.ResponseEntity<java.util.List<com.example.user_service.exception.ValidationError>>;
     * ResponseEntity with list of ValidationError and HTTP status 400 (BAD_REQUEST)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationError>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        logger.warn("Method argument validation failed: MethodArgumentNotValidException caught. Total field errors: {}",
                exception.getBindingResult().getFieldErrors().size());

        List<ValidationError> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    logger.info("Field validation error - Field: {}, Message: {}, Rejected value: {}",
                            error.getField(), error.getDefaultMessage(), error.getRejectedValue());

                    return new ValidationError(HttpStatus.BAD_REQUEST.value(),
                            "field: " + error.getField() + ", message: " + error.getDefaultMessage());
                })
                .toList();

        logger.warn("Request rejected due to method argument validation errors. Details: {}", validationErrors);
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    /**
     * @ Method Name: handleValidationExceptions
     * @ Description: handles MethodArgumentTypeMismatchException exceptions thrown when method arguments fail type validation
     * (e.g., String type instead of Long type for ID)
     * @ param      : [org.springframework.web.method.annotation.MethodArgumentTypeMismatchException]
     * @ return     : org.springframework.http.ResponseEntity<com.example.common_models.exception.ValidationError>
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ValidationError> handleValidationExceptions(MethodArgumentTypeMismatchException exception) {
        logger.warn("Method argument type validation failed: MethodArgumentTypeMismatchException caught: {}",
                exception.getMessage());

        ValidationError validationError = new ValidationError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());

        logger.warn("Request rejected due to method argument type validation errors. Details: {}", validationError);
        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    /**
     * @ Method Name: catchResourceNotFoundException
     * @ Description: handles UserNotFoundException exceptions when a requested resource is not found
     * @ param      : [com.example.user_service.exception.UserNotFoundException]
     * @ return     : org.springframework.http.ResponseEntity<com.example.user_service.exception.ValidationError>;
     * ResponseEntity with single ValidationError and HTTP status 404 (NOT_FOUND)
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ValidationError> catchUserNotFoundException(UserNotFoundException exception) {
        logger.warn("Resource not found: {}", exception.getMessage());

        ValidationError validationError = new ValidationError(HttpStatus.NOT_FOUND.value(), exception.getMessage());

        logger.warn("Resource not found exception handled. Returning 404 response: {}", validationError);
        return new ResponseEntity<>(validationError, HttpStatus.NOT_FOUND);
    }

}
