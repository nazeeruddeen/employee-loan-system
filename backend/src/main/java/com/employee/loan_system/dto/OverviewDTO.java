package com.employee.loan_system.dto;

import lombok.Data;

@Data
public class OverviewDTO {
    private Long appId;
    private String mobile;
    private String companyName;
    private String companyPan;
    private String mail;
    private Integer tenure;
    private Double loanAmt;
}
