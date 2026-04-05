package com.pm.authservice.service;


import com.pm.authservice.dto.request.LoginRequest;
import com.pm.authservice.dto.request.RefreshTokenRequest;
import com.pm.authservice.dto.request.RegisterRequest;
import com.pm.authservice.dto.response.LoginResponse;
import com.pm.authservice.dto.response.RefreshTokenResponse;
import com.pm.authservice.dto.response.RegisterResponse;

public interface AuthenticationService {
    RegisterResponse registerUser(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest loginRequest);

    RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    String logout(String refreshToken);

    boolean validateToken(String substring);
}
