package com.employee.loan_system.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SalesReportDTO {
    @NotBlank(message = "Date is required")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Date must be in DD/MM/YYYY format")
    private String date;

    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number must not exceed 50 characters")
    private String orderno;

    @NotBlank(message = "Invoice number is required")
    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    private String invoiceno;

    @NotBlank(message = "Party name is required")
    @Size(max = 100, message = "Party name must not exceed 100 characters")
    private String partyName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Party phone number must be exactly 10 digits")
    private String partyPhoneNum;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private Double totalAmount;

    @DecimalMin(value = "0.0", message = "Received/Paid amount must be non-negative")
    private Double recievedOrPaidAmount;

    @DecimalMin(value = "0.0", message = "Balance amount must be non-negative")
    private Double balanceAmount;
}
