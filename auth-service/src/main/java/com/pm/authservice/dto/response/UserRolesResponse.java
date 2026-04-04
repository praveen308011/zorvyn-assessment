package com.pm.authservice.dto.response;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRolesResponse {

    private UUID id;

    private String name;

    private Set<RoleData> roles;

    @Data
    @Builder
    public static class RoleData{
        private UUID id;
        private String name;
        private Set<String> permissions;
    }
}
