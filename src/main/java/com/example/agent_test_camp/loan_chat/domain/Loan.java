package com.example.agent_test_camp.loan_chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Loan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String loanNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  @JsonIgnore
  private Customer customer;

  private BigDecimal totalAmount;
  private BigDecimal paidAmount;

  public Loan() {}

  public Loan(String loanNumber, Customer customer, BigDecimal totalAmount, BigDecimal paidAmount) {
    this.loanNumber = loanNumber;
    this.customer = customer;
    this.totalAmount = totalAmount;
    this.paidAmount = paidAmount;
  }

  public Long getId() {
    return id;
  }

  public String getLoanNumber() {
    return loanNumber;
  }

  public void setLoanNumber(String loanNumber) {
    this.loanNumber = loanNumber;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public BigDecimal getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(BigDecimal paidAmount) {
    this.paidAmount = paidAmount;
  }
}
