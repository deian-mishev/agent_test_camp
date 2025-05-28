package com.example.agent_test_camp.loan_chat.dto;

import jakarta.validation.constraints.Size;

public record MessageDTO(
    @Size(max = 200, message = "Message must be 200 characters or less") String message) {}
