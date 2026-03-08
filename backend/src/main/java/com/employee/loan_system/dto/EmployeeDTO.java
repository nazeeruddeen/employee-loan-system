package com.employee.loan_system.dto;

import lombok.Data;

@Data
public class EmployeeDTO {
    private Long id;
    private String empCode;
    private String fname;
    private String lname;
    private String fullname;
    private String dept;
    private Integer age;
    private Double salary;
}
