package com.employee.loan_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company_address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", unique = true)
    private LoanApplication loanApplication;

    @Column(name = "flat_num")
    private String flatnum;

    @Column(name = "building")
    private String building;

    @Column(name = "line")
    private String line;

    @Column(name = "area")
    private String area;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "pincode")
    private String pincode;
}
