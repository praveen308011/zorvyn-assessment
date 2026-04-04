package com.pm.authservice.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {

    private String message;

    private UserData userData;


    @Data
    @Builder
    public static class UserData{
        private UUID id;
        private String name;
        private String email;
        private String status;
    }
}
