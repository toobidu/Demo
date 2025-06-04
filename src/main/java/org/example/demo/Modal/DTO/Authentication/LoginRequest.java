package org.example.demo.Modal.DTO.Authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class LoginRequest {
    @NotBlank(message = "Username không được để trống!")
    @Size(max = 200, message = "Username không được vượt quá 200 ký tự!")
    String username;

    @NotBlank(message = "Password không được để trống!")
    @Size(max = 20, message = "Password không được vụt quá 20 ký tự!")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "Password phải chứa ít nhất 6 ký tự, bao gồm 1 chữ hoa, 1 chữ thường, 1 số.")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    String password;
}
