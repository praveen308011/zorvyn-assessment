package com.pm.authservice.controller;

import com.pm.authservice.dto.request.LoginRequest;
import com.pm.authservice.dto.request.RefreshTokenRequest;
import com.pm.authservice.dto.request.RegisterRequest;
import com.pm.authservice.dto.response.LoginResponse;
import com.pm.authservice.dto.response.RefreshTokenResponse;
import com.pm.authservice.dto.response.RegisterResponse;
import com.pm.authservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
        return ResponseEntity.status(HttpStatus.OK)
                .body(authenticationService.registerUser(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.status(HttpStatus.OK)
                .body(authenticationService.login(loginRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.status(HttpStatus.OK)
                .body(authenticationService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(String refreshToken){
        return ResponseEntity.status(HttpStatus.OK)
                .body(authenticationService.logout(refreshToken));
    }

    // This end-point will be called from api-gateway for JWT verification
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader){
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return authenticationService.validateToken(authHeader.substring(7))
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
