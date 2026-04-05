package com.pm.financeservice.dto.request;

import com.pm.financeservice.model.enums.Category;
import com.pm.financeservice.model.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRecordRequest {

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Type cannot be null")
    private TransactionType type;

    @NotNull(message = "Category cannot be null")
    private Category category;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    private String notes;
}
