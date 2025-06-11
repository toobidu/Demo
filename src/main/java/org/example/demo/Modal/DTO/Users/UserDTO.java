package org.example.demo.Modal.DTO.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.demo.Modal.DTO.Finance.WalletDTO;
import org.example.demo.Modal.DTO.Orders.OrderDTO;

import java.util.List;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phone;
    private String passwordHash;
    private String typeAccount;
    private String rank;
    private List<RoleDTO> roles;
    private List<WalletDTO> wallets;
    private List<PermissionDTO> permissions;
    private List<OrderDTO> orders;
}
