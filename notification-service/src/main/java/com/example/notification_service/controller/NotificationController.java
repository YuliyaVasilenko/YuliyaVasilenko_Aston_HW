package com.example.notification_service.controller;


import com.example.common_models.event.UserEvent;
import com.example.common_models.event.UserOperation;
import com.example.notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 17-04-2026
 * Description: the class which describes REST API for sending notifications
 */
@Validated
@Tag(name = "Notification Management", description = "API for notification management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * @ Method Name: sendNotification
     * @ Description: sends a notification to the user to the required email about the required operation
     * @ param      : [java.lang.@jakarta.validation.constraints
     * .Email(message = "Incorrect email format") @jakarta.validation.constraints.NotBlank(message = "Email should not be blank") String,
     * com.example.common_models.event.@jakarta.validation.constraints.NotNull(message = "Operation should not be null") UserOperation]
     * @ return     : org.springframework.http.ResponseEntity<java.lang.Void>
     */
    @Operation(
            summary = "Send a notification",
            description = "Send a notification to the user to the required email " +
                    "with information about the required operation (create or delete)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notification was sent successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorList")))
            })
    @PostMapping()
    public ResponseEntity<Void> sendNotification(
            @Parameter(description = "User's email", example = "mike@gmail.com", required = true)
            @Email(message = "Incorrect email format")
            @NotBlank(message = "Email should not be blank")
            @RequestParam
            String email,

            @Parameter(description = "Operation that was required", required = true)
            @NotNull(message = "Operation should not be null")
            @RequestParam
            UserOperation operation) {
        notificationService.processUserEvent(new UserEvent(operation, email));

        return ResponseEntity.ok().build();
    }
}