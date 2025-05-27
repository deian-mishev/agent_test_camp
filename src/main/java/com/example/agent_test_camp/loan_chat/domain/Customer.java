package com.example.agent_test_camp.loan_chat.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "customers",
    uniqueConstraints = @UniqueConstraint(columnNames = {"first_name", "last_name"}))
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // or AUTO, SEQUENCE, etc.
  private Long id;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Loan> loans = new ArrayList<>();

  public Customer() {}

  public Customer(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public Long getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public List<Loan> getLoans() {
    return loans;
  }

  public void setLoans(List<Loan> loans) {
    this.loans = loans;
  }

  public void addLoan(Loan loan) {
    loans.add(loan);
    loan.setCustomer(this);
  }

  public void removeLoan(Loan loan) {
    loans.remove(loan);
    loan.setCustomer(null);
  }
}
