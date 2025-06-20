package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Users.UserDTO;

import java.util.List;

public interface IUserService {
    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);

    UserDTO getUser(Long id);

    List<UserDTO> getAllUsers(String typeAccount, String rank);
}
