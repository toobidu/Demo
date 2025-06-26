package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Users.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    @Query("SELECT p FROM Permission p WHERE p.permissionName = :permissionName")
    Optional<Permission> findByPermissionName(String permissionName);
}
