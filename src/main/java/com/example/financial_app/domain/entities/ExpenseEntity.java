package com.example.financial_app.domain.entities;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.example.financial_app.domain.enums.PaymentTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
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

  public static ExpenseEntity createDebit(
    String expenseName, 
    BigDecimal amount, 
    LocalDate paymentDate,
    Boolean isRecurring,
    Clock clock
  ) {
    Objects.requireNonNull(expenseName, "'expenseName' cannot be null");
    Objects.requireNonNull(amount, "'amount' cannot be null");
    Objects.requireNonNull(paymentDate, "'paymentDate' cannot be null");
    Objects.requireNonNull(isRecurring, "'isRecurring' cannot be null");
    Objects.requireNonNull(clock, "'clock' cannot be null");

    var currentDateTime = LocalDate.now(clock);

    return ExpenseEntity.builder()
      .description(expenseName)
      .amount(amount)
      .paymentType(PaymentTypeEnum.DEBIT)
      .isRecurring(isRecurring)
      .isIgnored(Boolean.FALSE)
      .isPaid(paymentDate.isBefore(currentDateTime) || paymentDate.isEqual(currentDateTime))
      .paymentDate(paymentDate)
      .build();
  }

  public static ExpenseEntity createCredit(
    String expenseName,
    BigDecimal amount,
    LocalDate paymentDate,
    Boolean isRecurring,
    CardEntity card,
    InvoiceEntity invoice,
    Clock clock
  ) {
    Objects.requireNonNull(expenseName, "'expenseName' cannot be null");
    Objects.requireNonNull(amount, "'amount' cannot be null");
    Objects.requireNonNull(paymentDate, "'paymentDate' cannot be null");
    Objects.requireNonNull(isRecurring, "'isRecurring' cannot be null");
    Objects.requireNonNull(card, "'card' cannot be null");
    Objects.requireNonNull(invoice, "'invoice' cannot be null");
    Objects.requireNonNull(clock, "'clock' cannot be null");

    var currentDateTime = LocalDate.now(clock);
    var isPaid = paymentDate.isBefore(currentDateTime) || paymentDate.isEqual(currentDateTime);

    return ExpenseEntity.builder()
      .description(expenseName)
      .amount(amount)
      .paymentType(PaymentTypeEnum.CREDIT)
      .isRecurring(isRecurring)
      .isIgnored(Boolean.FALSE)
      .card(card)
      .invoice(invoice)
      .isPaid(isPaid)
      .paymentDate(paymentDate)
      .build();
  }
}
