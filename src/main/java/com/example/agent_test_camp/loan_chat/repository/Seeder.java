package com.example.agent_test_camp.loan_chat.repository;

import com.example.agent_test_camp.loan_chat.domain.Customer;
import com.example.agent_test_camp.loan_chat.domain.Loan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class Seeder {

  @Bean
  CommandLineRunner seedDatabase(
      CustomerRepository customerRepository, LoanRepository loanRepository) {
    return args -> {
      Customer customer = new Customer("John", "Doe");
      customer = customerRepository.save(customer);

      Loan loan1 = new Loan();
      loan1.setLoanNumber("LN001");
      loan1.setTotalAmount(new BigDecimal("10000"));
      loan1.setPaidAmount(BigDecimal.ZERO);
      loan1.setCustomer(customer);
      loanRepository.save(loan1);

      Loan loan2 = new Loan();
      loan2.setLoanNumber("LN002");
      loan2.setTotalAmount(new BigDecimal("5000"));
      loan2.setPaidAmount(BigDecimal.ZERO);
      loan2.setCustomer(customer);
      loanRepository.save(loan2);

      customer.getLoans().add(loan1);
      customer.getLoans().add(loan2);
      customerRepository.save(customer);
    };
  }
}
