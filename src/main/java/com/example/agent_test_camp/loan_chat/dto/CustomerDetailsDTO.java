package com.example.agent_test_camp.loan_chat.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerDetailsDTO(
    @NotBlank(message = "First name is required") String firstName,
    @NotBlank(message = "Last name is required") String lastName) {}
