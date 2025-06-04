package org.example.demo.Modal.DTO.Authentication;

import lombok.experimental.FieldDefaults;
import org.example.demo.Modal.DTO.Users.UserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RegisterResponse {
    String message;
    UserDTO user;
    Boolean success;
}
