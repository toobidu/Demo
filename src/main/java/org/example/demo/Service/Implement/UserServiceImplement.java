package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.UserMapper;
import org.example.demo.Modal.DTO.Users.UserDTO;
import org.example.demo.Modal.Entity.Finance.Wallet;
import org.example.demo.Modal.Entity.Users.Role;
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Modal.Entity.Users.UserRole;
import org.example.demo.Modal.Entity.Users.UserRoleId;
import org.example.demo.Repository.RoleRepository;
import org.example.demo.Repository.UserRepository;
import org.example.demo.Repository.UserRoleRepository;
import org.example.demo.Repository.WalletRepository;
import org.example.demo.Service.Interface.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImplement implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating user: {}", userDTO.getUsername());
        validateUniqueUserName(userDTO.getUsername());
        validateUniqueEmail(userDTO.getEmail());

        User user = buildNewUserFromDTO(userDTO);
        user = userRepository.save(user);

        if (!walletRepository.existsById(user.getId())) {
            createWalletForUser(user);
        }

        // Tự động gán role dựa trên typeAccount
        assignRoleBasedOnTypeAccount(user);
        log.info("Đã gán role cho user ID: {}", user.getId());

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
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers(String typeAccount, String rank) {
        return userRepository.findAllWithFilters(typeAccount, rank)
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    // Helper methods

    private void validateUniqueUserName(String userName) {
        if (userRepository.findByUsername(userName).isPresent()) {
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
        wallet.setUserId(user.getId());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
        log.info("Wallet created for user ID: {}", user.getId());
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found: ID {}", id);
                    return new UserFriendlyException("User not found");
                });
    }

    private void updateUserFields(User user, UserDTO dto) {
        user.setUsername(dto.getUsername());
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
        return userRepository.findAll().stream()
                .filter(u -> safeEquals(u.getTypeAccount(), typeAccount) || typeAccount == null)
                .filter(u -> safeEquals(u.getRank(), rank) || rank == null)
                .collect(Collectors.toList());
    }

    // Helper để so sánh null-safe
    private boolean safeEquals(String a, String b) {
        return a != null && a.equals(b);
    }



    private void assignRoleBasedOnTypeAccount(User user) {
        try {
            String typeAccount = user.getTypeAccount();
            if (typeAccount == null || typeAccount.isEmpty()) {
                log.warn("TypeAccount is null or empty for user ID: {}", user.getId());
                return;
            }

            // Chuyển đổi typeAccount thành roleName
            String roleName = typeAccount.toUpperCase();
            log.info("Tìm role với roleName: {}", roleName);

            // Tìm role tương ứng
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> {
                        log.error("Không tìm thấy role với tên: {}", roleName);
                        return new UserFriendlyException("Không tìm thấy role tương ứng với typeAccount: " + typeAccount);
                    });

            // Tạo UserRole và thiết lập quan hệ
            UserRole userRole = new UserRole();
            UserRoleId userRoleId = new UserRoleId();
            userRoleId.setUserId(user.getId());
            userRoleId.setRoleId(role.getId());
            userRole.setId(userRoleId);
            userRole.setUser(user);
            userRole.setRole(role);

            // Lưu vào database
            userRoleRepository.save(userRole);
            log.info("Đã gán role {} cho user ID: {}", roleName, user.getId());
        } catch (Exception e) {
            log.error("Lỗi khi gán role cho user: ", e);
            throw e;
        }
    }
}
