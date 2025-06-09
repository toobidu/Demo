package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);

    @Query("SELECT p.permissionName FROM Permission p " +
            "JOIN RolePermission rp ON p.id = rp.permission.id " +
            "JOIN UserRole ur ON rp.role.id = ur.role.id " +
            "JOIN User u ON ur.user.id = u.id " +
            "WHERE u.userName = :username")
    Optional<String> findPermissionByUserName(String username);
}
