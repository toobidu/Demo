package org.example.demo.Modal.Entity.Users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Table(name = "permissions")
@Entity
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name", nullable = false)
    String name;
}
