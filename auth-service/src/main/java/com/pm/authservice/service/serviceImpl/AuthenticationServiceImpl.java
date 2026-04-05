package com.pm.authservice.service.serviceImpl;

import com.pm.authservice.config.JwtService;
import com.pm.authservice.dto.request.LoginRequest;
import com.pm.authservice.dto.request.RefreshTokenRequest;
import com.pm.authservice.dto.request.RegisterRequest;
import com.pm.authservice.dto.response.LoginResponse;
import com.pm.authservice.dto.response.RefreshTokenResponse;
import com.pm.authservice.dto.response.RegisterResponse;
import com.pm.authservice.exception.RefreshTokenExpiredException;
import com.pm.authservice.exception.UserNotFoundException;
import com.pm.authservice.model.RefreshToken;
import com.pm.authservice.model.User;
import com.pm.authservice.model.enums.Status;
import com.pm.authservice.repository.RefreshTokenRepository;
import com.pm.authservice.repository.UserRepository;
import com.pm.authservice.service.AuthenticationService;
import com.pm.authservice.service.RefreshTokenService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Override
    public RegisterResponse registerUser(RegisterRequest registerRequest) {

        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            log.error("Email with user already exists, Try using a different email address");
            throw new RuntimeException("Email with the user already exists, Try using different email address");
        }

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .status(Status.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        return RegisterResponse.builder()
                .message("Registration successful.")
                .userData(
                        RegisterResponse.UserData.builder()
                                .id(savedUser.getId())
                                .email(savedUser.getEmail())
                                .name(savedUser.getEmail())
                                .status(savedUser.getStatus().name())
                                .build()
                )
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()->{
                    log.error("User not found for the email : {}", loginRequest.getEmail());
                    return new UserNotFoundException("User not found for the respective email u have given");
                });

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword())
        );

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken token = refreshTokenService.rotateAndRefreshToken(refreshTokenRequest.getRefreshToken());

        User user = token.getUser();

        return RefreshTokenResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(token.getToken())
                .build();
    }

    @Override
    @Transactional
    public String logout(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        if(token.isEmpty()) {
            throw new RuntimeException("Invalid refresh token");
        }

        RefreshToken refreshToken1 = token.get();
        if (refreshToken1.isRevoked()) {
            throw new RefreshTokenExpiredException("Refresh token already used or revoked");
        }

        refreshToken1.setRevoked(true);
        refreshTokenRepository.save(refreshToken1);
       return "Logged out successfully."; // After logged out the front end should clear it's storage
    }

    @Override
    public boolean validateToken(String token) {
        try{
            jwtService.validateToken(token);
            return true;
        }
        catch(JwtException ex) {
            return false;
        }
    }
}
