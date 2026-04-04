package com.pm.authservice.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStatusResponse {

    private UUID id;

    private String message;

    private String status;
}
