package com.pm.authservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // CREATE_RECORD, VIEW_RECORD...

    private String description;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;
}
