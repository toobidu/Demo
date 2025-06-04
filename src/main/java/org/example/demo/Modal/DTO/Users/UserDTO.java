package org.example.demo.Modal.DTO.Users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.demo.Modal.DTO.Finance.WalletDTO;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserDTO {
    Long id;

    @NotBlank(message = "Username không được để trống!")
    @Size(max = 200, message = "Username không được vượt quá 200 ký tự!")
    String username;

    @NotBlank(message = "Password không được để trống!")
    @Size(max = 20, message = "Password không được vụt quá 20 ký tự!")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "Password phải chứa ít nhất 6 ký tự, bao gồm 1 chữ hoa, 1 chữ thường, 1 số.")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    String password;

    @NotBlank(message = "Firstname không được để trống!")
    @Size(max = 100, message = "Firstname không được vượt quá 100 ký tự!")
    String firstname;

    @NotBlank(message = "Lastname không được để trống!")
    @Size(max = 100, message = "Firstname không được vượt quá 100 ký tự!")
    String lastname;

    @NotBlank(message = "Phone không được để trống!")
    @Size(max = 100, message = "Firstname không được vượt quá 100 ký tự!")
    @Pattern(regexp = "^(0|\\+84)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$\n", message = "Phone phải là 10 số!")
    String phone;

    String address;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$\n", message = "Email phải đúng định dạng!")
    String email;

    String rankCode;

    String typeAccountCode;
    List<RoleDTO> roles;
    WalletDTO wallet;
}
