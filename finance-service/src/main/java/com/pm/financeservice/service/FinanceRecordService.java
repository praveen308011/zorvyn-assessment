package com.pm.financeservice.service;

import com.pm.financeservice.dto.request.CreateRecordRequest;
import com.pm.financeservice.dto.request.UpdateRecordRequest;
import com.pm.financeservice.dto.response.DeleteRecordResponse;
import com.pm.financeservice.dto.response.RecordResponse;
import com.pm.financeservice.model.enums.Category;
import com.pm.financeservice.model.enums.TransactionType;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FinanceRecordService {
    RecordResponse createRecord(@Valid CreateRecordRequest request);

    RecordResponse updateRecord(UUID id, @Valid UpdateRecordRequest request);

    DeleteRecordResponse deleteRecord(UUID id);

    List<RecordResponse> getAllRecords(Pageable pageable, Category category, TransactionType type, LocalDate startDate, LocalDate endDate);

    RecordResponse getRecord(UUID id);
}
