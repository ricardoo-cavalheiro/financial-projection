package com.example.financial_app.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

  public static InvoiceEntity create(CardEntity card, Integer month) {
    Objects.requireNonNull(card, "'card' cannot be null");
    Objects.requireNonNull(month, "'month' cannot be null");

    var currentDateTime = LocalDateTime.now().plusMonths(month);
    var currentDate = currentDateTime.toLocalDate();
    var maxDayInMonth = currentDate.lengthOfMonth();

    var safeClosingDay = Math.min(card.getClosingDay(), maxDayInMonth);
    var safePaymentDay = Math.min(card.getPaymentDay(), maxDayInMonth);
    var closingDate = currentDate.withDayOfMonth(safeClosingDay);
    var paymentDate = currentDate.withDayOfMonth(safePaymentDay);

    var isPaid = isInvoicePaid(currentDate, closingDate, month);

    return InvoiceEntity.builder()
        .card(card)
        .amount(BigDecimal.ZERO)
        .closingDate(closingDate)
        .paymentDate(paymentDate)
        .wasManuallyAdded(Boolean.FALSE)
        .isPaid(isPaid)
        .createdAt(currentDateTime)
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

  private static Boolean isInvoicePaid(LocalDate currentDate, LocalDate closingDate, Integer month) {
    if (month < 1 && closingDate.isBefore(currentDate)) {
      return true;
    } else {
      return false;
    }
  }
}
