package com.pm.financeservice.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrendsResponse {

    private List<MonthlyTrend> monthlyTrends;
    private List<WeeklyTrend> weeklyTrends;
    private String bestIncomeMonth;
    private String highestExpenseMonth;
    private Double averageMonthlyIncome;
    private Double averageMonthlyExpense;

    @Data
    @Builder
    public static class MonthlyTrend {
        private String month;        // "January 2024"
        private Integer year;
        private Double totalIncome;
        private Double totalExpense;
        private Double netBalance;
    }

    @Data
    @Builder
    public static class WeeklyTrend {
        private String week;         // "Week 1 - January 2024"
        private Double totalIncome;
        private Double totalExpense;
        private Double netBalance;
    }
}
