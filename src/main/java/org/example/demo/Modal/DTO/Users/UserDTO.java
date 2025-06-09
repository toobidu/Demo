package org.example.demo.Modal.DTO.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.demo.Modal.DTO.Finance.WalletDTO;
import org.example.demo.Modal.DTO.Orders.OrderDTO;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserDTO {
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
