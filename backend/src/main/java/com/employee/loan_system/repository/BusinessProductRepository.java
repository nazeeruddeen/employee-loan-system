package com.employee.loan_system.repository;

import com.employee.loan_system.entity.BusinessProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessProductRepository extends JpaRepository<BusinessProduct, Long> {
    Optional<BusinessProduct> findByLoanApplication_AppId(Long appId);
}
