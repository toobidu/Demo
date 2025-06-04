package org.example.demo.Modal.DTO.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserCreateDTO {
    String username;
    String password;
    String firstName;
    String lastName;
    String phone;
    String address;
    String email;
    String rankCode;
    String typeAccountCode;
}
