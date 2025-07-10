package com.example.financial_app.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class IncomeEntity {
  private Long id;
  private String description;
  private BigDecimal amount;
  private LocalDate paymentDate;
  private Integer recurrenceDay;
  private Boolean isRecurring;
  private LocalDateTime createdAt;
}
