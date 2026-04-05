package com.pm.financeservice.dto.request;

import com.pm.financeservice.model.enums.Category;
import com.pm.financeservice.model.enums.TransactionType;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRecordRequest {

    @Positive(message = "Amount must be positive")
    private Double amount;

    private TransactionType type;

    private Category category;

    private LocalDate date;

    private String notes;
}
