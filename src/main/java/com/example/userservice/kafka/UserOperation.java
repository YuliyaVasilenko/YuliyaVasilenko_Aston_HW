package com.example.userservice.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 16-04-2026
 * Description: the enum which describes events
 */
@Getter
@RequiredArgsConstructor
public enum UserOperation {
    CREATE("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан."),
    DELETE("Здравствуйте! Ваш аккаунт был удалён.");

    private final String message;
}
