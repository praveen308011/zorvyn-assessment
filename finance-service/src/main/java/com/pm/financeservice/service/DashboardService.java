package com.pm.financeservice.service;

import com.pm.financeservice.dto.response.*;

public interface DashboardService {
    SummaryResponse getSummary();

    CategoryTotalResponse getCategoryTotals();

    RecentActivityResponse getRecentActivity();

    TrendsResponse getTrends();

    AnalyticsResponse getAnalytics();

}
