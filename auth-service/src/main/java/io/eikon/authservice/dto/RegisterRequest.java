package io.eikon.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @Size(min = 2, message = "First name must be at least 2 characters")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @Size(min = 2, message = "Last name must be at least 2 characters")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "Role cannot be blank")
    private String role;
}
