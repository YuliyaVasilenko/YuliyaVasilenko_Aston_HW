package com.example.userservice.exception;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 11-04-2026
 * Description: This class describes the custom runtime exception thrown when a requested resource cannot be found
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

}
