package com.sahur_bot_3000.app.dto;

import com.sahur_bot_3000.app.model.Enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "Id is mandatory")
    private Long id;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    private String profilePictureUrl;

    @NotBlank(message = "Role is mandatory")
    private Role role;

    @NotBlank(message = "Google account is mandatory")
    private boolean googleAccount;
}
