package com.employee.loan_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "business_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", unique = true)
    private LoanApplication loanApplication;

    @Column(name = "purpose_of_loan", nullable = false)
    private String purposeOfLoan;

    @Column(name = "nature_of_business", nullable = false)
    private String natureOfBusiness;

    @Column(name = "loan_amount", nullable = false)
    private Double loanAmount;

    @Column(name = "tenure", nullable = false)
    private Integer tenure;
}
