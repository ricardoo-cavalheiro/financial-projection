package com.example.financial_app.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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

  public static ExpenseEntity create(
    String expenseName, 
    BigDecimal amount, 
    LocalDate paymentDate,
    PaymentTypeEnum paymentType,
    Boolean isRecurring
  ) {
    Objects.requireNonNull(expenseName, "'expenseName' cannot be null");
    Objects.requireNonNull(amount, "'amount' cannot be null");
    Objects.requireNonNull(paymentDate, "'paymentDate' cannot be null");
    Objects.requireNonNull(paymentType, "'paymentType' cannot be null");
    Objects.requireNonNull(isRecurring, "'isRecurring' cannot be null");

    var currentDate = LocalDate.now();
    var maxDayInMonth = paymentDate.lengthOfMonth();
    var safePaymentDay = Math.min(paymentDate.getDayOfMonth(), maxDayInMonth);
    var adjustedPaymentDate = paymentDate.withDayOfMonth(safePaymentDay);

    return ExpenseEntity.builder()
      .description(expenseName)
      .amount(amount)
      .paymentType(paymentType)
      .isRecurring(isRecurring)
      .isIgnored(Boolean.FALSE)
      .isPaid(adjustedPaymentDate.isBefore(currentDate) || adjustedPaymentDate.isEqual(currentDate))
      .paymentDate(adjustedPaymentDate)
      .build();
  } 
}
