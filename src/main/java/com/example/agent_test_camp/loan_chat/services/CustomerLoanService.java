package com.example.agent_test_camp.loan_chat.services;

import com.example.agent_test_camp.loan_chat.dto.CustomerDetailsDTO;
import com.example.agent_test_camp.loan_chat.dto.LoanDetailsDTO;
import com.example.agent_test_camp.loan_chat.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerLoanService {
  private final LoanRepository loanRepository;

  public CustomerLoanService(LoanRepository loanRepository) {
    this.loanRepository = loanRepository;
  }

  public List<LoanDetailsDTO> getLoansByCustomerFistLastName(
      CustomerDetailsDTO customerDetailsDTO) {
    return loanRepository
        .findByCustomerFirstNameAndCustomerLastName(
            customerDetailsDTO.firstName(), customerDetailsDTO.lastName())
        .stream()
        .map(
            loan ->
                new LoanDetailsDTO(
                    loan.getLoanNumber(), loan.getTotalAmount(), loan.getPaidAmount()))
        .toList();
  }
}
