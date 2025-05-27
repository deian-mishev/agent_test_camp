package com.example.agent_test_camp.loan_chat.services;

import com.example.agent_test_camp.loan_chat.domain.Customer;
import com.example.agent_test_camp.loan_chat.domain.Loan;
import com.example.agent_test_camp.loan_chat.dto.CustomerDetailsDTO;
import com.example.agent_test_camp.loan_chat.dto.LoanDetailsDTO;
import com.example.agent_test_camp.loan_chat.repository.CustomerRepository;
import com.example.agent_test_camp.loan_chat.repository.LoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerLoanService {
  private final CustomerRepository customerRepository;
  private final LoanRepository loanRepository;

  public CustomerLoanService(CustomerRepository customerRepository, LoanRepository loanRepository) {
    this.customerRepository = customerRepository;
    this.loanRepository = loanRepository;
  }

  @Transactional
  public Customer addCustomerWithLoan(
      String firstName, String lastName, String loanNumber, BigDecimal totalAmount) {
    Customer customer = new Customer(firstName, lastName);

    customer = customerRepository.save(customer);

    Loan loan = new Loan();
    loan.setLoanNumber(loanNumber);
    loan.setTotalAmount(totalAmount);
    loan.setPaidAmount(BigDecimal.ZERO);
    loan.setCustomer(customer);

    loanRepository.save(loan);
    customer.getLoans().add(loan);

    return customer;
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
