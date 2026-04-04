package com.pm.authservice.exception;

import io.jsonwebtoken.JwtException;

public class RefreshTokenExpiredException extends JwtException {
    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
