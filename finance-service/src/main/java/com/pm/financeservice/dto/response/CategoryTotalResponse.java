package com.pm.financeservice.dto.response;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryTotalResponse {
    private Map<String, Double> incomeByCategory;
    private Map<String, Double> expenseByCategory;
    private String highestIncomeCategory;
    private String highestExpenseCategory;
    private Double highestIncomeAmount;
    private Double highestExpenseAmount;
}
