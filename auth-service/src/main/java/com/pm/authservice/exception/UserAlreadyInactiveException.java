package com.pm.authservice.exception;

public class UserAlreadyInactiveException extends RuntimeException {
    public UserAlreadyInactiveException(String message) {
        super(message);
    }
}
