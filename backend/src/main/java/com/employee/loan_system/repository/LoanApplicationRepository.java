package com.employee.loan_system.repository;

import com.employee.loan_system.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    @Query("SELECT l FROM LoanApplication l WHERE l.status = 'PENDING'")
    List<LoanApplication> findAllPendingLoans();
}
