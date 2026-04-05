package com.pm.financeservice.dto.response;

import lombok.*;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteRecordResponse {
    private UUID id;
    private String message;
}
