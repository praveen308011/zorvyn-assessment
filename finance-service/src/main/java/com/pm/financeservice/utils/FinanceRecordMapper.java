package com.pm.financeservice.utils;

import com.pm.financeservice.dto.response.RecordResponse;
import com.pm.financeservice.model.FinanceRecord;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FinanceRecordMapper {

    public static RecordResponse mapToResponse(FinanceRecord savedRecord){
        return RecordResponse.builder()
                .id(savedRecord.getId())
                .amount(savedRecord.getAmount())
                .type(savedRecord.getType())
                .category(savedRecord.getCategory())
                .date(savedRecord.getDate())
                .notes(savedRecord.getNotes())
                .createdByEmail(savedRecord.getCreatedByEmail())
                .createdAt(savedRecord.getCreatedAt())
                .updatedAt(savedRecord.getUpdatedAt())
                .build();
    }
}
