package org.example.demo.Modal.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserDTO {
    Long id;
    String userName;
    String firstName;
    String lastName;
    String email;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    String address;
    String phone;
    String password;
    String fullName;
    Set<RoleDTO> roles;
}
