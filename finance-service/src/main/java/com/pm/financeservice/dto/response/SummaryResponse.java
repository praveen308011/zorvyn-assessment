package com.pm.financeservice.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SummaryResponse {
    private Double totalIncome;
    private Double totalExpense;
    private Double netBalance;
    private Long totalTransactions;
    private Double averageTransactionAmount;
    private String topSpendingCategory;
    private String topIncomeCategory;
    private LocalDate periodStart;
    private LocalDate periodEnd;
}
