package com.employee.loan_system.repository;

import com.employee.loan_system.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByLoanApplication_AppId(Long appId);

    @Query("SELECT t FROM Transaction t WHERE t.loanApplication.appId = :appId " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByAppIdAndDateRange(Long appId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM Transaction t WHERE t.loanApplication.appId = :appId " +
            "AND t.transactionStatus IN :statusList")
    List<Transaction> findByAppIdAndStatusIn(Long appId, List<String> statusList);

    @Query("SELECT t FROM Transaction t WHERE t.loanApplication.appId = :appId " +
            "AND t.instrument IN :instrumentList")
    List<Transaction> findByAppIdAndInstrumentIn(Long appId, List<String> instrumentList);
}
