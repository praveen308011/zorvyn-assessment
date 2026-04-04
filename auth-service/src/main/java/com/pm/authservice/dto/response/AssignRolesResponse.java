package com.pm.authservice.dto.response;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssignRolesResponse {

    private String message;

    private UUID userId;

    private Set<String> roles;
}
