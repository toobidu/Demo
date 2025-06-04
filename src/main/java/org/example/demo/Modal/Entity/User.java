package org.example.demo.Modal.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Table(name = "users")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "username", nullable = false, unique = true)
    String username;

    @Column(name = "password", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    String password;

    @Column(name = "firstname", nullable = false)
    String firstname;

    @Column(name = "lastname", nullable = false)
    String lastname;

    @Column(name = "phone", nullable = false)
    String phone;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "rank_code", nullable = false)
    String rankCode;

    @Column(name = "type_account_code", nullable = false)
    String typeAccountCode;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "users")
    Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "users")
    Set<RefreshToken> refreshTokens = new HashSet<>();

    @OneToMany(mappedBy = "users")
    Set<Wallet> wallets = new HashSet<>();

    @OneToMany(mappedBy = "users")
    Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "users")
    Set<Transaction> transactions = new HashSet<>();
}
