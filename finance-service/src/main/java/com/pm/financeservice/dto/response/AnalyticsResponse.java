package com.pm.financeservice.dto.response;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsResponse {
    private Double savingsRate;              // (income - expense) / income * 100
    private Double expenseToIncomeRatio;     // expense / income * 100
    private Map<String, Double> expenseBreakdownPercentage;
    private Map<String, Double> incomeBreakdownPercentage;
    private Double monthOverMonthIncomeGrowth;    // % change from last month
    private Double monthOverMonthExpenseGrowth;   // % change from last month
    private String financialHealthStatus;         // HEALTHY, WARNING, CRITICAL
}
