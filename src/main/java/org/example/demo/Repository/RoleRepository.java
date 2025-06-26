package org.example.demo.Repository;

import org.example.demo.Modal.DTO.Users.PermissionDTO;
import org.example.demo.Modal.Entity.Users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);

    @Query("SELECT p.permissionName FROM Permission p " +
            "JOIN RolePermission rp ON p.id = rp.id.permissionId " +
            "JOIN Role r ON rp.id.roleId = r.id " +
            "WHERE r.roleName = :roleName")
    List<String> findPermissionNamesByRoleName(@Param("roleName") String roleName);

    @Query("SELECT new org.example.demo.Modal.DTO.Users.PermissionDTO(p.id, p.permissionName, p.description) " +
            "FROM RolePermission rp " +
            "JOIN Permission p ON p.id = rp.id.permissionId " +
            "WHERE rp.id.roleId IN :roleIds")
    List<PermissionDTO> getPermissionsByRoleIds(@Param("roleIds") Set<Long> roleIds);
}
