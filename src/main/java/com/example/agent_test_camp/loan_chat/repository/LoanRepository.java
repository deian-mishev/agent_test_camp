package com.example.agent_test_camp.loan_chat.repository;

import com.example.agent_test_camp.loan_chat.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
  List<Loan> findByCustomerFirstNameAndCustomerLastName(String firstName, String lastName);
}
