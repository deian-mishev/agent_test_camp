package com.example.agent_test_camp.loan_chat.controllers;

import com.example.agent_test_camp.loan_chat.dto.MessageDTO;
import com.example.agent_test_camp.loan_chat.services.LoanAssistantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loans")
public class LoanAssistantController {

  private final LoanAssistantService loanAssistantService;

  public LoanAssistantController(LoanAssistantService lonAssistantService) {
    this.loanAssistantService = lonAssistantService;
  }

  @GetMapping
  public ResponseEntity<String> chat(HttpSession session, @Validated MessageDTO message) {
    return ResponseEntity.ok(loanAssistantService.chat(session.getId(), message.message()));
  }
}
