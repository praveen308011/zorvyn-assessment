package com.pm.authservice.exception;

public class UserAlreadyActiveException extends RuntimeException {
  public UserAlreadyActiveException(String message) {
    super(message);
  }
}
