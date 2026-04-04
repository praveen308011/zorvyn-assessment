package com.pm.authservice.service;


import com.pm.authservice.model.RefreshToken;
import com.pm.authservice.model.User;

public interface RefreshTokenService {

     RefreshToken rotateAndRefreshToken(String oldToken);

    RefreshToken createRefreshToken(User user);
}
