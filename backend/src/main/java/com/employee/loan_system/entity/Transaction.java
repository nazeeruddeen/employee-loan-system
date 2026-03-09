package com.employee.loan_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    private LoanApplication loanApplication;

    @NotNull(message = "Transaction date is required")
    @Column(name = "transaction_date", columnDefinition = "DATETIME")
    private LocalDateTime transactionDate;

    @NotBlank(message = "Activity is required")
    @Size(max = 100, message = "Activity must not exceed 100 characters")
    @Column(name = "activity")
    private String activity;

    @NotNull(message = "Transaction ID is required")
    @Column(name = "txn_id")
    private Long txnId;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    @Column(name = "comment")
    private String comment;

    @DecimalMin(value = "0.0", message = "Debit amount must be non-negative")
    @Column(name = "debt_amount")
    private Double debtAmt;

    @DecimalMin(value = "0.0", message = "Credit amount must be non-negative")
    @Column(name = "credit_amount")
    private Double creditAmt;

    @Size(max = 1000, message = "Transaction breakup must not exceed 1000 characters")
    @Column(name = "transaction_breakup")
    private String transactionBreakup;

    @NotBlank(message = "Transaction status is required")
    @Pattern(regexp = "^(SUCCESS|PENDING|FAILED|CANCELLED|COMPLETED|PROCESSING|ERROR)$",
             message = "Status must be one of: SUCCESS, PENDING, FAILED, CANCELLED, COMPLETED, PROCESSING, ERROR")
    @Column(name = "transaction_status")
    private String transactionStatus;

    @NotBlank(message = "Instrument is required")
    @Pattern(regexp = "^(CREDITCARD|DEBITCARD|UPI|WALLET|CASH|BANK_TRANSFER|CHEQUE)$",
             message = "Instrument must be one of: CREDITCARD, DEBITCARD, UPI, WALLET, CASH, BANK_TRANSFER, CHEQUE")
    @Column(name = "instrument")
    private String instrument;
}
