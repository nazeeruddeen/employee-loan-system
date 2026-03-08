package com.employee.loan_system.repository;

import com.employee.loan_system.entity.CompanyAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyAddressRepository extends JpaRepository<CompanyAddress, Long> {
    Optional<CompanyAddress> findByLoanApplication_AppId(Long appId);
}
