package com.example.financial_app.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentTypeEnum {
  CREDIT("credit"),
  DEBIT("debit");

  private final String value;
}
