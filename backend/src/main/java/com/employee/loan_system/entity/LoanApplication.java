package com.employee.loan_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_id")
    private Long appId;

    @Column(name = "first_name", nullable = false)
    private String fname;

    @Column(name = "last_name", nullable = false)
    private String lname;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "mail_id", nullable = false)
    private String mailId;

    @Column(name = "mobile", nullable = false)
    private String mobile;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "application_date")
    private LocalDateTime applicationDate = LocalDateTime.now();

    @Column(name = "status")
    private String status = "PENDING";

    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CompanyDetails companyDetails;

    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CompanyAddress companyAddress;

    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BusinessProduct businessProduct;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.fname != null && this.lname != null) {
            this.customerName = this.fname.trim() + " " + this.lname.trim();
        }
    }
}
