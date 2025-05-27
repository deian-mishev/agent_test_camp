package com.example.agent_test_camp.loan_chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record LoanDetailsDTO(
    @NotBlank(message = "Loan number is required")
        @Pattern(regexp = "^[0-9]+$", message = "Loan number must contain only digits")
        String loanNumber,
    BigDecimal totalAmount,
    BigDecimal paidAmount) {}
