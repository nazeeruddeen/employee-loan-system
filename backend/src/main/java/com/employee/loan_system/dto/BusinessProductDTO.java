package com.employee.loan_system.dto;

import lombok.Data;

@Data
public class BusinessProductDTO {
    private String purposeOfLoan;
    private String natureOfBusiness;
    private Double loanAmount;
    private Integer tenure;
}
