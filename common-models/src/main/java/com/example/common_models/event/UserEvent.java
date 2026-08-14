package com.example.common_models.event;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 17-04-2026
 * Description: the class which describes an event to send to user
 */
public record UserEvent(UserOperation operation, String email) {
}
