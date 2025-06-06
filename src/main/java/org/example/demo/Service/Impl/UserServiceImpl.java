package org.example.demo.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.UserMapper;
import org.example.demo.Modal.DTO.RegisterRequest;
import org.example.demo.Modal.DTO.UserDTO;
import org.example.demo.Modal.Entity.User;
import org.example.demo.Repository.UserRepository;
import org.example.demo.Service.Interface.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(RegisterRequest request) {
        //Kiểm tra username là duy nhất
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new UserFriendlyException("Username đã tồn tại!");
        }
        
        // Kiểm tra email là duy nhất
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserFriendlyException("Email đã tồn tại!");
        }
        
        // Create new user
        User user = new User();
        user.setUserName(request.getUserName());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFirstName() + " " + request.getLastName());
        
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("User not found"));
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserFriendlyException("User not found"));
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("User not found"));
        
        // Update fields
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setFullName(userDTO.getFirstName() + " " + userDTO.getLastName());
        
        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserFriendlyException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> searchUsers(String keyword) {
        return userRepository.findByFullNameContainingIgnoreCase(keyword).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}