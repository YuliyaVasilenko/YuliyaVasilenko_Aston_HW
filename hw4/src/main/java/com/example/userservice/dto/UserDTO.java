package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 10-04-2026
 * Description: This class is used to exchange the data between application layers (controller, service, view)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDTO {

    @Positive
    private Long id;

    @NotBlank(message = "Name should not be blank")
    private String name;

    @Email(message = "Incorrect email format")
    @NotBlank(message = "Email should not be blank")
    private String email;

    @Min(value = 1, message = "Age should be positive")
    @Max(value = 100, message = "Age should not be more than 100")
    private Integer age;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    public UserDTO(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

}
