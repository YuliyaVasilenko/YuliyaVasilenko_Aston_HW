package com.example.userservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 11-04-2026
 * Description: This class provides information about data validation errors
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {

    private int statusCode;

    private String message;

}
