package com.pm.financeservice.exception.handler;

import com.pm.financeservice.dto.response.ErrorResponse;
import com.pm.financeservice.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex){
        log.error("Check once what you've given in the fields for the request {}", ex.getMessage());

        return this.buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
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
