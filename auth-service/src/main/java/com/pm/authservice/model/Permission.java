package com.pm.authservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name; // CREATE_RECORD, VIEW_RECORD...

    private String description;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;
}
