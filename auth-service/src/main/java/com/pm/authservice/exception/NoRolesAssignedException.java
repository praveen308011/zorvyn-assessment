package com.pm.authservice.exception;

public class NoRolesAssignedException extends RuntimeException {
    public NoRolesAssignedException(String message) {
        super(message);
    }
}
