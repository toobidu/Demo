package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByTypeAccount(String typeAccount);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.userRoles ur " +
            "LEFT JOIN FETCH ur.role r " +
            "LEFT JOIN FETCH r.rolePermissions rp " +
            "LEFT JOIN FETCH rp.permission p")
    Page<User> findAllWithDetails(Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.userRoles ur " +
            "LEFT JOIN FETCH ur.role r " +
            "LEFT JOIN FETCH r.rolePermissions rp " +
            "LEFT JOIN FETCH rp.permission p " +
            "WHERE (:typeAccount IS NULL OR u.typeAccount = :typeAccount) " +
            "AND (:rank IS NULL OR u.rank = :rank)")
    List<User> findAllWithFilters(
            @Param("typeAccount") String typeAccount,
            @Param("rank") String rank);

    @Query("SELECT p.permissionName from Permission p " +
            "JOIN RolePermission rp ON p.id = rp.id.permissionId  " +
            "JOIN UserRole ur ON rp.id.roleId = ur.id.roleId " +
            "WHERE ur.id.userId = :userId")
    Set<String> findPermissionsByUserId(@Param("userId") Long userId);
}

