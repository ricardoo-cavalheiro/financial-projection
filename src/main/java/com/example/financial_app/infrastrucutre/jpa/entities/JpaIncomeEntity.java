package com.example.financial_app.infrastrucutre.jpa.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.financial_app.domain.entities.IncomeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "IncomeEntity")
@Table(name = "income")
public class JpaIncomeEntity extends IncomeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "description")
  private String description;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "payment_date")
  private LocalDate paymentDate;

  @Column(name = "recurrence_day")
  private Integer recurrenceDay;

  @Column(name = "is_recurring")
  private Boolean isRecurring;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;
}
