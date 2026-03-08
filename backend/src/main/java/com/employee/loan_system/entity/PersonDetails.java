package com.employee.loan_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "person_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    private LoanApplication loanApplication;

    @NotBlank(message = "Employee name is required")
    @Size(max = 100, message = "Employee name must not exceed 100 characters")
    @Column(name = "ename", nullable = false)
    private String ename;

    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    @Column(name = "nationality")
    private String nationality;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must not exceed 100")
    @Column(name = "age")
    private Integer age;

    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "mail")
    private String mail;

    @Pattern(regexp = "^(male|female|others)$", message = "Gender must be 'male', 'female', or 'others'")
    @Column(name = "gender")
    private String gender;
}
