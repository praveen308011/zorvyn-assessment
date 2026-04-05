package com.pm.financeservice.service.serviceImpl;

import com.pm.financeservice.dto.request.CreateRecordRequest;
import com.pm.financeservice.dto.request.UpdateRecordRequest;
import com.pm.financeservice.dto.response.DeleteRecordResponse;
import com.pm.financeservice.dto.response.RecordResponse;
import com.pm.financeservice.exception.BadRequestException;
import com.pm.financeservice.exception.handler.BadRequestException;
import com.pm.financeservice.model.FinanceRecord;
import com.pm.financeservice.model.enums.Category;
import com.pm.financeservice.model.enums.TransactionType;
import com.pm.financeservice.repository.FinanceRecordRepository;
import com.pm.financeservice.service.FinanceRecordService;
import com.pm.financeservice.specification.FinanceSpecification;
import com.pm.financeservice.utils.FinanceRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.pm.financeservice.utils.FinanceRecordMapper.mapToResponse;

@Service
@RequiredArgsConstructor
@Slf4j
class FinanceRecordServiceImpl implements FinanceRecordService {

    private final FinanceRecordRepository financeRecordRepository;



    @Override
    public RecordResponse createRecord(CreateRecordRequest request) {
        log.info("Creating record by user: {}", "admin@gmail.com"); // as of now In future we'll perform inter-service communication
        // communicating with auth-service to get the user with them and assigning the name or email of them

        FinanceRecord record = FinanceRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .createdByEmail("admin@gmail.com")
                .deleted(false)
                .build();

        FinanceRecord savedRecord = financeRecordRepository.save(record);
        log.info("Record created with id: {}", savedRecord.getId());

        return mapToResponse(savedRecord);
    }

    @Override
    public RecordResponse updateRecord(UUID id, UpdateRecordRequest request) {

        FinanceRecord record = findRecordById(id);

        this.updateIfNotNull(request.getAmount(), record::setAmount);
        this.updateIfNotNull(request.getType(), record::setType);
        this.updateIfNotNull(request.getCategory(), record::setCategory);
        this.updateIfNotNull(request.getDate(), record::setDate);
        this.updateIfNotNull(request.getNotes(), record::setNotes);

        return mapToResponse(record);
    }

    @Override
    public DeleteRecordResponse deleteRecord(UUID id) {
        FinanceRecord record = this.findRecordById(id);

        record.setDeleted(true);
        financeRecordRepository.save(record);

        return DeleteRecordResponse.builder()
                .id(id)
                .message("Record delete successfully")
                .build();
    }

    @Override
    public List<RecordResponse> getAllRecords(Pageable pageable, Category category, TransactionType type, LocalDate startDate, LocalDate endDate) {

        if(startDate!=null && endDate!=null && startDate.isAfter(endDate)){
            throw new BadRequestException("Start date must be before end date");
        }

        Specification<FinanceRecord> specification = FinanceSpecification.getSpecification(category, type, startDate, endDate);

        return financeRecordRepository.findAll(specification, pageable).getContent()
                .stream()
                .map(FinanceRecordMapper::mapToResponse)
                .toList();
    }

    @Override
    public RecordResponse getRecord(UUID id) {
        return mapToResponse(this.findRecordById(id));
    }


    // Helper methods

    private FinanceRecord findRecordById(UUID id){
        return financeRecordRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(()-> new BadRequestException.RecordNotFoundException("Record not found with id: "+ id));

    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter){
        if(value!=null) setter.accept(value);
    }

}
