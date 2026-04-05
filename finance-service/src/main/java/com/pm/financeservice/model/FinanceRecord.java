package com.pm.financeservice.model;

import com.pm.financeservice.model.enums.Category;
import com.pm.financeservice.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "finance_record")
public class FinanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;       // INCOME or EXPENSE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;          // SALARY, FOOD, RENT...

    @Column(nullable = false)
    private LocalDate date;

    private String notes;

    // who created this record
    @Column(nullable = false)
    private String createdByEmail;      // from X-User-Email header or X-User-ID

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean deleted = false;


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
