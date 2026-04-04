package com.pm.authservice.exception;

import io.jsonwebtoken.JwtException;

public class JwtTokenMissingException extends JwtException {
    public JwtTokenMissingException(String message) {
        super(message);
    }
}
