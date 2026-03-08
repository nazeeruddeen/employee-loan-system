package com.employee.loan_system.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TransactionDTO {
    private Long id;
    
    @NotNull(message = "Application ID is required")
    private Long appid;
    
    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;
    
    @NotBlank(message = "Activity is required")
    @Size(max = 100, message = "Activity must not exceed 100 characters")
    private String activity;
    
    @NotNull(message = "Transaction ID is required")
    private Long txnId;
    
    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;
    
    @DecimalMin(value = "0.0", message = "Debit amount must be non-negative")
    private Double debtAmt;
    
    @DecimalMin(value = "0.0", message = "Credit amount must be non-negative")
    private Double creditAmt;
    
    @Size(max = 1000, message = "Transaction breakup must not exceed 1000 characters")
    private String transactionBreakup;
    
    @NotBlank(message = "Transaction status is required")
    @Pattern(regexp = "^(SUCCESS|PENDING|FAILED|CANCELLED|COMPLETED|PROCESSING|ERROR)$", 
             message = "Status must be one of: SUCCESS, PENDING, FAILED, CANCELLED, COMPLETED, PROCESSING, ERROR")
    private String transactionStatus;
    
    @NotBlank(message = "Instrument is required")
    @Pattern(regexp = "^(CREDITCARD|DEBITCARD|UPI|WALLET|CASH|BANK_TRANSFER|CHEQUE)$", 
             message = "Instrument must be one of: CREDITCARD, DEBITCARD, UPI, WALLET, CASH, BANK_TRANSFER, CHEQUE")
    private String instrument;
}
