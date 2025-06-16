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

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByTypeAccount(String typeAccount);

    // Lấy danh sách quyền dưới dạng tên (nếu vẫn cần)
    @Query("""
        SELECT p.permissionName FROM Permission p
        JOIN RolePermission rp ON p.id = rp.permission.id
        JOIN UserRole ur ON rp.role.id = ur.role.id
        WHERE ur.user.id = :userId
    """)
    Set<String> findPermissionsByUserId(@Param("userId") Long userId);

    // Load đầy đủ để ánh xạ DTO
    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN FETCH u.userRoles ur
        LEFT JOIN FETCH ur.role r
        LEFT JOIN FETCH r.rolePermissions rp
        LEFT JOIN FETCH rp.permission
        LEFT JOIN FETCH u.wallets w
        WHERE (:typeAccount IS NULL OR u.typeAccount = :typeAccount)
          AND (:rank IS NULL OR u.rank = :rank)
    """)
    List<User> findUsersWithFullInfo(@Param("typeAccount") String typeAccount,
                                     @Param("rank") String rank);
}

