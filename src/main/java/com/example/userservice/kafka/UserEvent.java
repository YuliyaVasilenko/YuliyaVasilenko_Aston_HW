package com.example.userservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 16-04-2026
 * Description: the class which describes an event to send to user
 */
@Getter
@AllArgsConstructor
public class UserEvent {

    private UserOperation userOperation;

    private String email;
}
