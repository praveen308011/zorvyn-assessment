package com.pm.financeservice.service.serviceImpl;

import com.pm.financeservice.dto.response.*;
import com.pm.financeservice.model.FinanceRecord;
import com.pm.financeservice.model.enums.TransactionType;
import com.pm.financeservice.repository.FinanceRecordRepository;
import com.pm.financeservice.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
class DashboardServiceImpl implements DashboardService {

    private final FinanceRecordRepository financeRecordRepository;

    @Override
    public SummaryResponse getSummary() {

        Double totalIncome = financeRecordRepository.getTotalByType(TransactionType.INCOME);
        Double totalExpense = financeRecordRepository.getTotalByType(TransactionType.EXPENSE);
        Double netBalance = totalIncome - totalExpense;
        Long totalTransactions = financeRecordRepository.countByDeletedFalse();
        Double avgAmount = financeRecordRepository.getAverageTransactionAmount();

        List<Object[]> categoryTotals = financeRecordRepository
                .getCategoryWiseTotals();

        String topSpendingCategory = findTopCategory(
                categoryTotals, TransactionType.EXPENSE);
        String topIncomeCategory = findTopCategory(
                categoryTotals, TransactionType.INCOME);

        return SummaryResponse.builder()
                .totalIncome(roundToTwoDecimals(totalIncome))
                .totalExpense(roundToTwoDecimals(totalExpense))
                .netBalance(roundToTwoDecimals(netBalance))
                .totalTransactions(totalTransactions)
                .averageTransactionAmount(roundToTwoDecimals(avgAmount))
                .topSpendingCategory(topSpendingCategory)
                .topIncomeCategory(topIncomeCategory)
                .periodStart(LocalDate.now().withDayOfYear(1))
                .periodEnd(LocalDate.now())
                .build();
    }

    @Override
    public CategoryTotalResponse getCategoryTotals() {
        log.info("Fetching category totals");

        List<Object[]> categoryTotals = financeRecordRepository
                .getCategoryWiseTotals();

        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();

        for (Object[] row : categoryTotals) {
            String category = row[0].toString();
            TransactionType type = TransactionType.valueOf(row[1].toString());
            Double amount = ((Number) row[2]).doubleValue();

            if (type == TransactionType.INCOME) {
                incomeByCategory.put(category,
                        roundToTwoDecimals(amount));
            } else {
                expenseByCategory.put(category,
                        roundToTwoDecimals(amount));
            }
        }

        String highestIncomeCategory = findHighestKey(incomeByCategory);
        String highestExpenseCategory = findHighestKey(expenseByCategory);

        return CategoryTotalResponse.builder()
                .incomeByCategory(incomeByCategory)
                .expenseByCategory(expenseByCategory)
                .highestIncomeCategory(highestIncomeCategory)
                .highestExpenseCategory(highestExpenseCategory)
                .highestIncomeAmount(incomeByCategory
                        .getOrDefault(highestIncomeCategory, 0.0))
                .highestExpenseAmount(expenseByCategory
                        .getOrDefault(highestExpenseCategory, 0.0))
                .build();
    }

    @Override
    public RecentActivityResponse getRecentActivity() {
        log.info("Fetching recent activity");


        List<FinanceRecord> records = financeRecordRepository
                .findTop10ByDeletedFalseOrderByCreatedAtDesc();

        List<RecentActivityResponse.ActivityItem> activities = records
                .stream()
                .map(r -> RecentActivityResponse.ActivityItem.builder()
                        .id(r.getId())
                        .amount(r.getAmount())
                        .type(r.getType())
                        .category(r.getCategory())
                        .date(r.getDate())
                        .notes(r.getNotes())
                        .createdByEmail(r.getCreatedByEmail())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();

        return RecentActivityResponse.builder()
                .activities(activities)
                .totalCount(activities.size())
                .build();
    }

    @Override
    public TrendsResponse getTrends() {
        log.info("Fetching trends");

        List<Object[]> monthlyRaw = financeRecordRepository.getMonthlyTrends();
        List<Object[]> weeklyRaw = financeRecordRepository.getWeeklyTrends();

        // for Building monthly trends
        Map<String, TrendsResponse.MonthlyTrend> monthlyMap = new LinkedHashMap<>();

        for (Object[] row : monthlyRaw) {
            int month = ((Number) row[0]).intValue();
            int year = ((Number) row[1]).intValue();
            TransactionType type = TransactionType.valueOf(row[2].toString());
            Double amount = ((Number) row[3]).doubleValue();

            String key = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year;

            monthlyMap.putIfAbsent(key, TrendsResponse.MonthlyTrend.builder()
                    .month(key)
                    .year(year)
                    .totalIncome(0.0)
                    .totalExpense(0.0)
                    .netBalance(0.0)
                    .build());

            TrendsResponse.MonthlyTrend trend = monthlyMap.get(key);

            if (type == TransactionType.INCOME) {
                trend.setTotalIncome(roundToTwoDecimals(amount));
            } else {
                trend.setTotalExpense(roundToTwoDecimals(amount));
            }
            trend.setNetBalance(roundToTwoDecimals(trend.getTotalIncome() - trend.getTotalExpense()));
        }

        List<TrendsResponse.MonthlyTrend> monthlyTrends = new ArrayList<>(monthlyMap.values());


        // for building weekly trends
        Map<String, TrendsResponse.WeeklyTrend> weeklyMap = new LinkedHashMap<>();

        for (Object[] row : weeklyRaw) {
            int week = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            int year = ((Number) row[2]).intValue();
            TransactionType type = TransactionType.valueOf(row[3].toString());
            Double amount = ((Number) row[4]).doubleValue();

            String key = "Week " + week + " - " +
                    Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year;

            weeklyMap.putIfAbsent(key, TrendsResponse.WeeklyTrend.builder()
                    .week(key)
                    .totalIncome(0.0)
                    .totalExpense(0.0)
                    .netBalance(0.0)
                    .build());

            TrendsResponse.WeeklyTrend trend = weeklyMap.get(key);

            if (type == TransactionType.INCOME) {
                trend.setTotalIncome(roundToTwoDecimals(amount));
            } else {
                trend.setTotalExpense(roundToTwoDecimals(amount));
            }
            trend.setNetBalance(roundToTwoDecimals(trend.getTotalIncome() - trend.getTotalExpense()));
        }

        List<TrendsResponse.WeeklyTrend> weeklyTrends = new ArrayList<>(weeklyMap.values());

        // best Income/Expense Month
        String bestIncomeMonth = monthlyTrends.stream()
                .max(Comparator.comparingDouble(
                        TrendsResponse.MonthlyTrend::getTotalIncome))
                .map(TrendsResponse.MonthlyTrend::getMonth)
                .orElse("N/A");

        String highestExpenseMonth = monthlyTrends.stream()
                .max(Comparator.comparingDouble(
                        TrendsResponse.MonthlyTrend::getTotalExpense))
                .map(TrendsResponse.MonthlyTrend::getMonth)
                .orElse("N/A");

        // avg monthly Income/Expense
        Double avgMonthlyIncome = monthlyTrends.stream()
                .mapToDouble(TrendsResponse.MonthlyTrend::getTotalIncome)
                .average()
                .orElse(0.0);

        Double avgMonthlyExpense = monthlyTrends.stream()
                .mapToDouble(TrendsResponse.MonthlyTrend::getTotalExpense)
                .average()
                .orElse(0.0);

        return TrendsResponse.builder()
                .monthlyTrends(monthlyTrends)
                .weeklyTrends(weeklyTrends)
                .bestIncomeMonth(bestIncomeMonth)
                .highestExpenseMonth(highestExpenseMonth)
                .averageMonthlyIncome(roundToTwoDecimals(avgMonthlyIncome))
                .averageMonthlyExpense(roundToTwoDecimals(avgMonthlyExpense))
                .build();
    }

    @Override
    public AnalyticsResponse getAnalytics() {
        log.info("Fetching analytics");

        Double totalIncome = financeRecordRepository.getTotalByType(TransactionType.INCOME);
        Double totalExpense = financeRecordRepository.getTotalByType(TransactionType.EXPENSE);

        Double savingsRate = totalIncome>0 ? roundToTwoDecimals((totalIncome - totalExpense) / totalIncome * 100) : 0.0;

        //  expense to income ratio
        Double expenseToIncomeRatio = totalIncome>0 ? roundToTwoDecimals(totalExpense / totalIncome * 100) : 0.0;

        List<Object[]> categoryTotals = financeRecordRepository.getCategoryWiseTotals();

        // expense and income breakdown
        Map<String, Double> expenseBreakdown = new HashMap<>();
        Map<String, Double> incomeBreakdown = new HashMap<>();

        for (Object[] row : categoryTotals) {
            String category = row[0].toString();
            TransactionType type = TransactionType.valueOf(row[1].toString());
            Double amount = ((Number) row[2]).doubleValue();

            if (type == TransactionType.EXPENSE && totalExpense > 0) {
                expenseBreakdown.put(category,
                        roundToTwoDecimals(amount / totalExpense * 100));
            } else if (type == TransactionType.INCOME && totalIncome > 0) {
                incomeBreakdown.put(category,
                        roundToTwoDecimals(amount / totalIncome * 100));
            }
        }

        // Month-over-Month growth
        LocalDate now = LocalDate.now();
        LocalDate thisMonthStart = now.withDayOfMonth(1);
        LocalDate lastMonthStart = thisMonthStart.minusMonths(1);
        LocalDate lastMonthEnd = thisMonthStart.minusDays(1);

        Double thisMonthIncome = financeRecordRepository.getTotalByTypeAndDateRange(
                TransactionType.INCOME, thisMonthStart, now);
        Double lastMonthIncome = financeRecordRepository.getTotalByTypeAndDateRange(
                TransactionType.INCOME, lastMonthStart, lastMonthEnd);
        Double thisMonthExpense = financeRecordRepository.getTotalByTypeAndDateRange(
                TransactionType.EXPENSE, thisMonthStart, now);
        Double lastMonthExpense = financeRecordRepository.getTotalByTypeAndDateRange(
                TransactionType.EXPENSE, lastMonthStart, lastMonthEnd);

        Double incomeGrowth = calculateGrowth(
                lastMonthIncome, thisMonthIncome);
        Double expenseGrowth = calculateGrowth(
                lastMonthExpense, thisMonthExpense);

        // ── financial health ──
        String financialHealth = determineFinancialHealth(
                savingsRate, expenseToIncomeRatio);

        return AnalyticsResponse.builder()
                .savingsRate(savingsRate)
                .expenseToIncomeRatio(expenseToIncomeRatio)
                .expenseBreakdownPercentage(expenseBreakdown)
                .incomeBreakdownPercentage(incomeBreakdown)
                .monthOverMonthIncomeGrowth(incomeGrowth)
                .monthOverMonthExpenseGrowth(expenseGrowth)
                .financialHealthStatus(financialHealth)
                .build();
    }


    // Helper Methods

    private Double roundToTwoDecimals(Double value) {
        if (value == null) return 0.0;
        return Math.round(value * 100.0) / 100.0;
    }

    private String findTopCategory(
            List<Object[]> data, TransactionType targetType) {
        return data.stream()
                .filter(row -> TransactionType.valueOf(
                        row[1].toString()) == targetType)
                .max(Comparator.comparingDouble(
                        row -> ((Number) row[2]).doubleValue()))
                .map(row -> row[0].toString())
                .orElse("N/A");
    }

    private String findHighestKey(Map<String, Double> map) {
        return map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private Double calculateGrowth(Double previous, Double current) {
        if (previous == null || previous == 0.0) return 0.0;
        return roundToTwoDecimals(
                ((current - previous) / previous) * 100);
    }

    private String determineFinancialHealth(Double savingsRate, Double expenseRatio) {
        if (savingsRate >= 20) {
            return "HEALTHY";
        } else if (savingsRate >= 5) {
            return "WARNING";
        } else {
            return "CRITICAL";
        }
    }
}
