package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user theo userName (dùng cho đăng nhập/đăng ký)
    Optional<User> findByUserName(String userName);

    // Tìm user theo email (dùng để kiểm tra trùng email khi đăng ký)
    Optional<User> findByEmail(String email);

    // Lọc user theo typeAccount (admin, sale, print_house)
    List<User> findByTypeAccount(String typeAccount);

    // Lấy danh sách quyền của user dựa trên userId
    @Query("SELECT p.permissionName FROM Permission p " +
            "JOIN RolePermission rp ON p.id = rp.permission.id " +
            "JOIN UserRole ur ON rp.role.id = ur.role.id " +
            "WHERE ur.user.id = :userId")
    Set<String> findPermissionsByUserId(@Param("userId") Long userId);
}
