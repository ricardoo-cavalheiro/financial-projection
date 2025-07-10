package com.example.financial_app.domain.entities;

import java.math.BigDecimal;
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
@ToString(exclude = "invoices")
public class CardEntity {
  private Long id;
  private String name;
  private BigDecimal limitAmount;
  private Integer closingDay;
  private Integer paymentDay;
  private List<InvoiceEntity> invoices;
}
