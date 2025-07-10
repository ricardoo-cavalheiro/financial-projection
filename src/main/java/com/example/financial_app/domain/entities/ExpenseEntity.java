package com.example.financial_app.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.financial_app.domain.enums.PaymentTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ExpenseEntity {
  private Long id;
  private String description;
  private BigDecimal amount;
  private PaymentTypeEnum paymentType;
  private Boolean isRecurring;
  private Boolean isPaid;
  private Integer totalInstallments;
  private Integer installmentNumber;
  private LocalDate paymentDate;
  private LocalDateTime createdAt;
  private Boolean isIgnored;
  private CardEntity card;
  private InvoiceEntity invoice;
}
