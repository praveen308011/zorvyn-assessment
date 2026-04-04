package com.pm.authservice.service.serviceImpl;

import com.pm.authservice.model.RefreshToken;
import com.pm.authservice.model.User;
import com.pm.authservice.repository.RefreshTokenRepository;
import com.pm.authservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.token}")
    private String refreshExpirationDuration;

    public RefreshToken rotateAndRefreshToken(String oldToken){

        RefreshToken existingToken = refreshTokenRepository.findByToken(oldToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if(existingToken.isRevoked()) throw new RuntimeException("Token already Revoked");

        if(existingToken.getExpiryDate().isBefore(Instant.now())) throw new RuntimeException("Token Expired");

        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);

        RefreshToken newRefreshToken = createRefreshToken(existingToken.getUser());

        return refreshTokenRepository.save(newRefreshToken);
    }

    @Override
    public RefreshToken createRefreshToken(User user) {
        return RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(Long.parseLong(refreshExpirationDuration)))
                .user(user)
                .revoked(false)
                .build();
    }
}
