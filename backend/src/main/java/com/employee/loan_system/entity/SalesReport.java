package com.employee.loan_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    private LoanApplication loanApplication;

    @NotBlank(message = "Date is required")
    @Column(name = "date")
    private String date;

    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number must not exceed 50 characters")
    @Column(name = "order_no")
    private String orderno;

    @NotBlank(message = "Invoice number is required")
    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    @Column(name = "invoice_no")
    private String invoiceno;

    @NotBlank(message = "Party name is required")
    @Size(max = 100, message = "Party name must not exceed 100 characters")
    @Column(name = "party_name")
    private String partyName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Party phone number must be exactly 10 digits")
    @Column(name = "party_phone_num")
    private String partyPhoneNum;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Column(name = "total_amount")
    private Double totalAmount;

    @DecimalMin(value = "0.0", message = "Received/Paid amount must be non-negative")
    @Column(name = "received_paid_amount")
    private Double recievedOrPaidAmount;

    @DecimalMin(value = "0.0", message = "Balance amount must be non-negative")
    @Column(name = "balance_amount")
    private Double balanceAmount;
}
