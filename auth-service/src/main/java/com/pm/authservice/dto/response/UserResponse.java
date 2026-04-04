package com.pm.authservice.dto.response;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private UUID id;

    private String name;

    private String email;

    private String status;

    private Set<String> roles;
}
