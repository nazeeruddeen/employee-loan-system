package com.employee.loan_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", unique = true)
    private LoanApplication loanApplication;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "industry_type", nullable = false)
    private String industryType;

    @Column(name = "date_of_establish", nullable = false)
    private String dateOfEstablish;

    @Column(name = "turnover", nullable = false)
    private Double turnover;

    @Column(name = "company_pan", nullable = false, length = 10)
    private String companyPan;

    @Column(name = "gstin", length = 15)
    private String gstin;
}
