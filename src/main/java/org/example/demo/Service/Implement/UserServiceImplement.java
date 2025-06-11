package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.UserMapper;
import org.example.demo.Modal.DTO.Users.UserDTO;
import org.example.demo.Modal.Entity.Finance.Wallet;
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Repository.UserRepository;
import org.example.demo.Repository.WalletRepository;
import org.example.demo.Service.Interface.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImplement implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating user: {}", userDTO.getUserName());
        validateUniqueUserName(userDTO.getUserName());
        validateUniqueEmail(userDTO.getEmail());

        User user = buildNewUserFromDTO(userDTO);
        user = userRepository.save(user);

        createWalletForUser(user);

        log.info("User created with ID: {}", user.getId());
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Updating user ID: {}", id);
        User user = findUserById(id);
        updateUserFields(user, userDTO);
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        log.info("User updated: ID {}", id);
        return userMapper.toDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user ID: {}", id);
        findUserById(id); // để throw nếu không tồn tại
        userRepository.deleteById(id);
        log.info("User deleted: ID {}", id);
    }

    @Override
    public UserDTO getUser(Long id) {
        log.info("Retrieving user ID: {}", id);
        User user = findUserById(id);
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers(String typeAccount, String rank) {
        log.info("Retrieving users with typeAccount: {}, rank: {}", typeAccount, rank);
        List<User> users = filterUsers(typeAccount, rank);
        return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    // Tách nhỏ logic thành các hàm

    private void validateUniqueUserName(String userName) {
        if (userRepository.findByUserName(userName).isPresent()) {
            log.error("Username already exists: {}", userName);
            throw new UserFriendlyException("Username already exists");
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.error("Email already exists: {}", email);
            throw new UserFriendlyException("Email already exists");
        }
    }

    private User buildNewUserFromDTO(UserDTO dto) {
        User user = userMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPasswordHash()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private void createWalletForUser(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found: ID {}", id);
                    return new UserFriendlyException("User not found");
                });
    }

    private void updateUserFields(User user, UserDTO dto) {
        user.setUserName(dto.getUserName());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        user.setPhone(dto.getPhone());
        user.setTypeAccount(dto.getTypeAccount());
        user.setRank(dto.getRank());
        if (dto.getPasswordHash() != null && !dto.getPasswordHash().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPasswordHash()));
        }
    }

    private List<User> filterUsers(String typeAccount, String rank) {
        if (typeAccount != null && rank != null) {
            return userRepository.findAll().stream()
                    .filter(u -> u.getTypeAccount().equals(typeAccount) && u.getRank().equals(rank))
                    .collect(Collectors.toList());
        } else if (typeAccount != null) {
            return userRepository.findByTypeAccount(typeAccount);
        } else if (rank != null) {
            return userRepository.findAll().stream()
                    .filter(u -> u.getRank().equals(rank))
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll();
        }
    }
}
