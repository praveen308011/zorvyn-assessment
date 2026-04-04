package com.pm.authservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private Integer status;
    private String message;
    private LocalDateTime timeStamp;
}
