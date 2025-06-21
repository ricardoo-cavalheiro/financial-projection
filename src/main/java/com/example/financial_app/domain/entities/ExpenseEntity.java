package com.example.financial_app.domain.entities;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.example.financial_app.domain.enums.PaymentTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;

@Builder
@Entity(name = "expense")
@Table(name = "expense")
public class ExpenseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "description")
  private String description;

  @Column(name = "amount")
  private Double amount;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "payment_type", columnDefinition = "expense_kind")
  private PaymentTypeEnum paymentType;

  @Column(name = "is_recurring")
  private Boolean isRecurring;

  @Column(name = "recurrence_day")
  private Integer recurrenceDay;

  @Column(name = "installments")
  private Integer installments;

  @Column(name = "current_installment")
  private Integer currentInstallment;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "card_id")
  private CardEntity card;
}
