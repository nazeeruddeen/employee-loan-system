package com.employee.loan_system.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PersonDetailsDTO {
    private Long id;

    @NotBlank(message = "Employee name is required")
    @Size(max = 100, message = "Employee name must not exceed 100 characters")
    private String ename;

    @NotBlank(message = "Nationality is required")
    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must not exceed 100")
    private Integer age;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String mail;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(male|female|others)$", message = "Gender must be 'male', 'female', or 'others'")
    private String gender;
}
