package com.example.financial_app.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Objects;

import com.example.financial_app.domain.enums.PaymentTypeEnum;

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

  public static InvoiceEntity create(CardEntity card, Month month) {
    Objects.requireNonNull(card, "Card cannot be null");
    Objects.requireNonNull(month, "Month cannot be null");

    var currentDate = LocalDateTime.now();
    var closingDate = currentDate.withDayOfMonth(card.getClosingDay()).plusMonths(month.getValue());
    var paymentDate = currentDate.withDayOfMonth(card.getPaymentDay()).plusMonths(month.getValue());

    return InvoiceEntity.builder()
        .card(card)
        .amount(BigDecimal.ZERO)
        .closingDate(closingDate.toLocalDate())
        .paymentDate(paymentDate.toLocalDate())
        .wasManuallyAdded(Boolean.FALSE)
        .isPaid(currentDate.isAfter(closingDate))
        .createdAt(currentDate)
        .build();
  }

  public BigDecimal sumExpenses() {
    if (wasManuallyAdded) {
      return getAmount();
    }

    if (expenses == null || expenses.isEmpty()) {
      return BigDecimal.ZERO;
    }

    return expenses.stream()
        .filter(expense -> !expense.getIsIgnored())
        .filter(expense -> expense.getPaymentType().equals(PaymentTypeEnum.CREDIT))
        .map(ExpenseEntity::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void updateAmount(BigDecimal newAmount) {
    Objects.requireNonNull(newAmount, "'newAmount' cannot be null");

    setAmount(newAmount);
    setWasManuallyAdded(Boolean.TRUE);
  }

  public void setAsPaid(Boolean isPaid) {
    Objects.requireNonNull(isPaid, "'isPaid' cannot be null");

    setIsPaid(isPaid);
  }
}
