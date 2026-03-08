package com.employee.loan_system.dto;

import lombok.Data;

@Data
public class CompanyDetailsDTO {
    private String companyName;
    private String industryType;
    private String dateOfEstablish;
    private Double turnover;
    private String companyPan;
    private String gstin;
}
