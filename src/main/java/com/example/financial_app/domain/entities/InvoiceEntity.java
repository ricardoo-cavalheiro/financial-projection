package com.example.financial_app.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
public class InvoiceEntity {
  private Long id;
  private BigDecimal amount;
  private Boolean isPaid;
  private LocalDate closingDate;
  private LocalDate paymentDate;
  private Boolean wasManuallyAdded;
  private LocalDateTime createdAt;
  private CardEntity card;
  private List<ExpenseEntity> expenses;
}
