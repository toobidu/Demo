package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Users.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    @Query("SELECT ur.user.id FROM UserRole ur WHERE ur.role.id = :roleId")
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);
}

