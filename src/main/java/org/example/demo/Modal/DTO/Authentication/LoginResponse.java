package org.example.demo.Modal.DTO.Authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.demo.Modal.DTO.Users.UserDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class LoginResponse {
    String accessToken;
    String refreshToken;
    String tokenType = "Bearer";
    LocalDateTime expiresAt;
    UserDTO user;
}
