package com.pm.financeservice.dto.response;

import com.pm.financeservice.model.enums.Category;
import com.pm.financeservice.model.enums.TransactionType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecordResponse {
    private UUID id;
    private Double amount;
    private TransactionType type;
    private Category category;
    private LocalDate date;
    private String notes;
    private String createdByEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
