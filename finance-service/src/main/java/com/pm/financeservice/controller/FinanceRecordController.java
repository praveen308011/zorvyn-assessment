package com.pm.financeservice.controller;

import com.pm.financeservice.dto.request.CreateRecordRequest;
import com.pm.financeservice.dto.request.UpdateRecordRequest;
import com.pm.financeservice.dto.response.DeleteRecordResponse;
import com.pm.financeservice.dto.response.RecordResponse;
import com.pm.financeservice.model.enums.Category;
import com.pm.financeservice.model.enums.TransactionType;
import com.pm.financeservice.service.FinanceRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/records")
public class FinanceRecordController {

    private final FinanceRecordService financeRecordService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecordResponse> createRecord(@RequestBody @Valid CreateRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(financeRecordService.createRecord(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecordResponse> updateRecord(@PathVariable UUID id, @RequestBody @Valid UpdateRecordRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(financeRecordService.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeleteRecordResponse> deleteRecord(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(financeRecordService.deleteRecord(id));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<List<RecordResponse>> getAllRecords(@RequestParam(required = false, defaultValue = "1") int pageNumber,
                                                              @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                              @RequestParam(required = false) String sortByField,
                                                              @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
                                                              @RequestParam(required = false) Category category,
                                                              @RequestParam(required = false) TransactionType type,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Sort sort = null;
        if(sortDirection.equalsIgnoreCase("ASC")) sort = Sort.by(sortByField).ascending();
        else sort = Sort.by(sortByField).ascending();

        return ResponseEntity.status(HttpStatus.OK).body(financeRecordService.getAllRecords(
                PageRequest.of(pageNumber-1, pageSize, sort), category, type, startDate, endDate));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<RecordResponse> getRecord(
            @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(financeRecordService.getRecord(id));
    }


}
