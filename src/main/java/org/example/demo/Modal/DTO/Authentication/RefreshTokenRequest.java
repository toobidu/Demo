package org.example.demo.Modal.DTO.Authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token không được để trống")
    String refreshToken;
}
