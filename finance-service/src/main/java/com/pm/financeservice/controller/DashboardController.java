package com.pm.financeservice.controller;

import com.pm.financeservice.dto.response.*;
import com.pm.financeservice.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<SummaryResponse> getSummary(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(dashboardService.getSummary());
    }

    @GetMapping("/category-totals")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<CategoryTotalResponse> getCategoryTotals(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(dashboardService.getCategoryTotals());
    }

    // ✅ ALL roles
    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<RecentActivityResponse> getRecentActivity() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dashboardService.getRecentActivity());
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<TrendsResponse> getTrends() {
        return ResponseEntity.ok(dashboardService.getTrends());
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(dashboardService.getAnalytics());
    }


}
