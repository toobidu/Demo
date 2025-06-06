package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.UserDTO;
import org.example.demo.Modal.DTO.RegisterRequest;

import java.util.List;

public interface IUserService {
    UserDTO createUser(RegisterRequest request);
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    List<UserDTO> getAllUsers();
    List<UserDTO> getUsersByRole(String roleName);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    List<UserDTO> searchUsers(String keyword);
}