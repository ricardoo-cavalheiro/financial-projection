package com.example.financial_app.infrastrucutre.jpa.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import lombok.Data;

@Data
@Entity(name = "ExpenseEntity")
@Table(name = "expense", schema = "prd")
public class JpaExpenseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "description")
  private String description;

  @Column(name = "amount")
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "payment_type", columnDefinition = "expense_kind")
  private PaymentTypeEnum paymentType;

  @Column(name = "is_recurring")
  private Boolean isRecurring;

  @Column(name = "is_paid")
  private Boolean isPaid;

  @Column(name = "total_installments")
  private Integer totalInstallments;

  @Column(name = "installment_number")
  private Integer installmentNumber;

  @Column(name = "payment_date")
  private LocalDate paymentDate;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "is_ignored")
  private Boolean isIgnored;

  @ManyToOne
  @JoinColumn(name = "card_id")
  private JpaCardEntity card;

  @ManyToOne
  @JoinColumn(name = "invoice_id")
  private JpaInvoiceEntity invoice;
}
