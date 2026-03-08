package com.employee.loan_system.dto;

import lombok.Data;

@Data
public class LoanApplicationDTO {
    private Long appId;
    private String fname;
    private String lname;
    private String customerName;
    private String mailId;
    private String mobile;
    private String city;
    private String status;
}
