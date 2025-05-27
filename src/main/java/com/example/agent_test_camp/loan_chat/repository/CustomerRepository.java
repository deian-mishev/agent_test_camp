package com.example.agent_test_camp.loan_chat.repository;

import com.example.agent_test_camp.loan_chat.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByFirstNameAndLastName(String firstName, String lastName);
}