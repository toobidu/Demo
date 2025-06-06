package org.example.demo.Modal.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    private String address;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String fullName;

    @NotBlank
    private String userName;

    @Email
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;
}
