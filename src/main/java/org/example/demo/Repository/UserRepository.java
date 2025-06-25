package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Users.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role", "userRoles.role.rolePermissions", "userRoles.role.rolePermissions.permission"})
    @Query("SELECT u FROM User u")
    List<User> findAll();

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role", "userRoles.role.rolePermissions", "userRoles.role.rolePermissions.permission"})
//    @Query("""
//        SELECT u.typeAccount FROM User u
//        WHERE u.id = :id
//    """)
    List<User> findByTypeAccount(String typeAccount);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("""
                SELECT p.permissionName FROM Permission p
                JOIN RolePermission rp ON p.id = rp.permission.id
                JOIN UserRole ur ON ur.role.id = rp.role.id
                WHERE ur.user.id = :userId
            """)
    Set<String> findPermissionsByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role", "userRoles.role.rolePermissions", "userRoles.role.rolePermissions.permission"})
    @Query("SELECT u FROM User u")
    List<User> findAllWithPermissions();

    @Query("SELECT DISTINCT u FROM User u " + "LEFT JOIN FETCH u.userRoles ur " + "LEFT JOIN FETCH ur.role r " + "LEFT JOIN FETCH r.rolePermissions rp " + "LEFT JOIN FETCH rp.permission " + "WHERE (:typeAccount is null OR u.typeAccount = :typeAccount) " + "AND (:rank is null OR u.rank = :rank)")
    List<User> findAllWithFilters(@Param("typeAccount") String typeAccount, @Param("rank") String rank);
}

