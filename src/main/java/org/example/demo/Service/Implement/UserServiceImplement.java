package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.UserMapper;
import org.example.demo.Modal.DTO.Users.UserDTO;
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Repository.UserRepository;
import org.example.demo.Service.Interface.IUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImplement implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Tạo người dùng mới: {}", userDTO.getUserName());
        User user = userMapper.toEntity(userDTO);
        user = userRepository.save(user);
        log.info("Người dùng được tạo với id: {}", user.getId());
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO updatedUser(Long id, UserDTO userDTO) {
        log.info("Cập nhật người dùng với ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Không tìm thấy người dùng: ID {}", id);
                    return new UserFriendlyException("Không tìm thấy người dùng!");
                });
        user.setUserName(userDTO.getUserName());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setAddress(userDTO.getAddress());
        user.setPhone(userDTO.getPhone());
        user.setTypeAccount(userDTO.getTypeAccount());
        user.setRank(userDTO.getRank());
        user = userRepository.save(user);
        log.info("Cập nhật người dùng: ID {}", id);
        return userMapper.toDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Xóa người dùng với ID: {}", id);
        userRepository.deleteById(id);
        log.info("Xóa người dùng thành công: ID {}", id);
    }

    @Override
    public UserDTO getUser(Long id) {
        log.info("Truy vấn tới người dùng có ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Không tìm thấy người dùng: ID {}", id);
                    return new UserFriendlyException("Không tìm thấy người dùng!");
                });
        log.info("Truy vấn tới người dùng thành công: ID {}", id);
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers(String typeAccount, String rank) {
        log.info("Retrieving users with typeAccount: {}, rank: {}", typeAccount, rank);
        List<User> users;
        if (typeAccount != null && rank != null) {
            users = userRepository.findAll().stream()
                    .filter(u -> u.getTypeAccount().equals(typeAccount) && u.getRank().equals(rank))
                    .collect(Collectors.toList());
        } else if (typeAccount != null) {
            users = userRepository.findByTypeAccount(typeAccount);
        } else if (rank != null) {
            users = userRepository.findAll().stream()
                    .filter(u -> u.getRank().equals(rank))
                    .collect(Collectors.toList());
        } else {
            users = userRepository.findAll();
        }
        return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }
}
