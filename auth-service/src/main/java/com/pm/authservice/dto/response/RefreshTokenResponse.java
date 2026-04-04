package com.pm.authservice.dto.response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenResponse{

    private String accessToken;

    private String refreshToken;
}
