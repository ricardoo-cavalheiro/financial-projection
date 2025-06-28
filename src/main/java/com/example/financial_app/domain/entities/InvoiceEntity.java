package com.example.financial_app.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = "expenses")
@Entity(name = "InvoiceEntity")
@Table(name = "invoice")
public class InvoiceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "is_paid")
  private Boolean isPaid;

  @Column(name = "closing_date")
  private LocalDate closingDate;

  @Column(name = "payment_date")
  private LocalDate paymentDate;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "card_id")
  private CardEntity card;

  @OneToMany(mappedBy = "invoice")
  private List<ExpenseEntity> expenses;
}
