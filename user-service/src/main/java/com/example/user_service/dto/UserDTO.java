package com.example.user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 10-04-2026
 * Description: This class is used to exchange the data between application layers (controller, service, view)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "A model for representing the user")
public class UserDTO extends RepresentationModel<UserDTO> {

    @Positive
    @Schema(description = "User ID", example = "1")
    private Long id;

    @NotBlank(message = "Name should not be blank")
    @Schema(description = "User name", example = "Mike")
    private String name;

    @Email(message = "Incorrect email format")
    @NotBlank(message = "Email should not be blank")
    @Schema(description = "User's email address", example = "mike@gmail.com")
    private String email;

    @Min(value = 1, message = "Age should be positive")
    @Max(value = 100, message = "Age should not be more than 100")
    @Schema(description = "User's age", example = "25")
    private Integer age;

    @Schema(description = "The user creation date", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime created_at;

    @Schema(description = "The date of the last update of user data", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updated_at;

    public UserDTO(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

}
