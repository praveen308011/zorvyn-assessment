package com.pm.financeservice.specification;

import com.pm.financeservice.model.FinanceRecord;
import com.pm.financeservice.model.enums.Category;
import com.pm.financeservice.model.enums.TransactionType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanceSpecification {
    public static Specification<FinanceRecord> getSpecification(Category category,
                                                                TransactionType type,
                                                                LocalDate startDate,
                                                                LocalDate endDate){
        return new Specification<FinanceRecord>() {
            @Override
            public Predicate toPredicate(@NonNull Root<FinanceRecord> root,
                                         CriteriaQuery<?> query,
                                         @NonNull CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicateList = new ArrayList<>();

                predicateList.add(criteriaBuilder.isFalse(root.get("deleted")));

                if(category!=null && !category.toString().isEmpty()){
                    predicateList.add(criteriaBuilder.equal(root.get("category"), category));
                }

                if(type!=null && !type.toString().isEmpty()){
                    predicateList.add(criteriaBuilder.equal(root.get("type"), type));
                }

                if(startDate!=null && endDate!=null){
                    predicateList.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate),
                            criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate)));
                }

                return criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
            }
        };
    }
}
