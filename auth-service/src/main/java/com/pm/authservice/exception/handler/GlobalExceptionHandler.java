package com.pm.authservice.exception.handler;

import com.pm.authservice.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.pm.authservice.dto.response.ErrorResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex){
        log.error("Credentials are wrong maybe Invalid {}", ex.getMessage());

        return this.buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.error("Access Denied ! {}", ex.getMessage());

        return this.buildError(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
        log.error("JWT Token has been expired {}", ex.getMessage());

        return this.buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenExpired(RefreshTokenExpiredException ex){
        log.error("Refresh token expired: {}", ex.getMessage());

        return this.buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex){
        log.error("User not found {}", ex.getMessage());

        return this.buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(RoleNotFoundException ex) {
        log.error("Role not found: {}", ex.getMessage());

        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({UserAlreadyActiveException.class, UserAlreadyInactiveException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {
        log.error("Conflict: {}", ex.getMessage());

        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentException(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err->err.getField()+":"+err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("Validation error: {}", message);

        return buildError(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage());

        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later.");
    }

    @ExceptionHandler(NoRolesAssignedException.class)
    public ResponseEntity<ErrorResponse> handleNoRolesException(NoRolesAssignedException ex){
        log.error("No Roles has been assigned to the user: {}", ex.getMessage());

        return this.buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }


    // Helper Method

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .status(status.value())
                        .message(message)
                        .timeStamp(LocalDateTime.now())
                        .build());
    }

}
