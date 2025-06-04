package org.example.demo.Modal.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Table(name = "roles")
@Entity
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name", nullable = false)
    String name;
}
