package com.employee.loan_system.repository;

import com.employee.loan_system.entity.PersonDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonDetailsRepository extends JpaRepository<PersonDetails, Long> {
    List<PersonDetails> findByLoanApplication_AppId(Long appId);
}
