package com.pm.financeservice.repository;

import com.pm.financeservice.model.FinanceRecord;

import com.pm.financeservice.model.enums.TransactionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinanceRecordRepository extends JpaRepository<FinanceRecord, UUID>, JpaSpecificationExecutor<FinanceRecord> {

    Optional<FinanceRecord> findByIdAndDeletedFalse(UUID id);

    @Query("""
SELECT COALESCE(SUM(f.amount), 0)
FROM FinanceRecord f
WHERE f.type = :type
AND f.deleted = false
""")
    Double getTotalByType(@Param("type") TransactionType type); // Dynamic Query to get either Total Income/Expense

    Long countByDeletedFalse();

    @Query("SELECT COALESCE(AVG(f.amount), 0) FROM FinanceRecord f " +
            "WHERE f.deleted = false")
    Double getAverageTransactionAmount();

    @Query("SELECT f.category, f.type, COALESCE(SUM(f.amount), 0) " +
            "FROM FinanceRecord f " +
            "WHERE f.deleted = false " +
            "GROUP BY f.category, f.type")
    List<Object[]> getCategoryWiseTotals();

    List<FinanceRecord> findTop10ByDeletedFalseOrderByCreatedAtDesc();

    @Query("SELECT MONTH(f.date), YEAR(f.date), f.type, " +
            "COALESCE(SUM(f.amount), 0) " +
            "FROM FinanceRecord f " +
            "WHERE f.deleted = false " +
            "GROUP BY YEAR(f.date), MONTH(f.date), f.type " +
            "ORDER BY YEAR(f.date) DESC, MONTH(f.date) DESC")
    List<Object[]> getMonthlyTrends();

    @Query("SELECT WEEK(f.date), MONTH(f.date), YEAR(f.date), f.type, " +
            "COALESCE(SUM(f.amount), 0) " +
            "FROM FinanceRecord f " +
            "WHERE f.deleted = false " +
            "GROUP BY YEAR(f.date), MONTH(f.date), WEEK(f.date), f.type " +
            "ORDER BY YEAR(f.date) DESC, MONTH(f.date) DESC")
    List<Object[]> getWeeklyTrends();

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinanceRecord f " +
            "WHERE f.type = :type " +
            "AND f.deleted = false " +
            "AND f.date BETWEEN :startDate AND :endDate")
    Double getTotalByTypeAndDateRange(@Param("type") TransactionType type,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);


}
