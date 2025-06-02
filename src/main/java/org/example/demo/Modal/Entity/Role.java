//package org.example.demo.Modal.Entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.experimental.FieldDefaults;
//
//import java.util.HashSet;
//import java.util.Set;
//
//
//@Table(name = "roles")
//@Entity
//@Data
//@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
//public class Role {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//
//    @Column(name = "name", nullable = false)
//    String name;
//
//    @ManyToMany
//    @JoinTable(
//            name = "role_permissions",
//            joinColumns = @JoinColumn(name = "role_id"),
//            inverseJoinColumns = @JoinColumn(name = "permission_id"))
//    Set<Permission> permissions = new HashSet<>();
//}
