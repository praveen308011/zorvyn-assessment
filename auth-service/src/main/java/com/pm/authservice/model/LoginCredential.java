package com.pm.authservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "login_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginCredential {  // I'll use this class later

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    private LocalDateTime lastLogin;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
