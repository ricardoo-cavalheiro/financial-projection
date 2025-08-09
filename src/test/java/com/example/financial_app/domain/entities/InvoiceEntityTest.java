package com.example.financial_app.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.financial_app.domain.enums.PaymentTypeEnum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@DisplayName("InvoiceEntity Tests")
public class InvoiceEntityTest {
  private final static Integer CLOSING_DAY = 15;
  private final static Integer PAYMENT_DAY = 20;
  private final static LocalDateTime CURRENT_DATE_TIME = LocalDateTime.now();

  @Nested
  @DisplayName("Happy path")
  class InvoiceEntityHappyPathTests {
    @Test
    @DisplayName("Should create invoice with valid card and month")
    void testCreateInvoice() {
      var clock = createClock();
      var currentDate = CURRENT_DATE_TIME.toLocalDate();

      var card = createCard();
      var month = YearMonth.now(clock); // Current month

      var invoice = createInvoiceEntity(card, month, clock);

      assertThat(invoice.getCard())
        .as("Invoice was set to card '%s'", invoice.getCard().getName())
        .isEqualTo(card);
      assertThat(invoice.getClosingDate()).isEqualTo(currentDate.withDayOfMonth(CLOSING_DAY));
      assertThat(invoice.getPaymentDate()).isEqualTo(currentDate.withDayOfMonth(PAYMENT_DAY));
      assertThat(invoice.getAmount()).isEqualTo(BigDecimal.ZERO);
      assertThat(invoice.getWasManuallyAdded()).isFalse();
      assertThat(invoice.getCreatedAt()).isEqualTo(CURRENT_DATE_TIME);
    }

    @Test
    @DisplayName("Should set closing date to last day of month if closing day is greater than last day of month")
    void testCreateInvoiceWithClosingDayGreaterThanLastDayOfMonth() {
      var clock = createClock();
      var month = YearMonth.of(2023, 2); // February 2023 (28 days)

      var card = CardEntity.builder()
        .id(1L)
        .name("Valid card name")
        .closingDay(30) // Invalid closing day for February
        .paymentDay(8)
        .build();

      var invoice = createInvoiceEntity(card, month, clock);

      assertThat(invoice.getClosingDate()).isEqualTo(month.atEndOfMonth());
    }

    @Test
    @DisplayName("Should set payment date to last day of month if payment day is greater than last day of month")
    void testCreateInvoiceWithPaymentDayGreaterThanLastDayOfMonth() {
      var clock = createClock();
      var month = YearMonth.of(2023, 2); // February 2023 (28 days)

      var card = CardEntity.builder()
        .id(1L)
        .name("Valid card name")
        .closingDay(CLOSING_DAY)
        .paymentDay(30) // Invalid payment day for February
        .build();

      var invoice = createInvoiceEntity(card, month, clock);

      assertThat(invoice.getPaymentDate()).isEqualTo(month.atEndOfMonth());
    }

    @Test
    @DisplayName("Should adjust payment date to next month if payment day is before closing day")
    void testCreateInvoiceWithPaymentDayBeforeClosingDay() {
      var clock = createClock();
      var month = YearMonth.of(2023, 2); // February 2023 (28 days)

      var card = CardEntity.builder()
        .id(1L)
        .name("Valid card name")
        .closingDay(29)
        .paymentDay(10) // Payment day before closing day
        .build();

      var invoice = createInvoiceEntity(card, month, clock);

      assertThat(invoice.getPaymentDate()).isEqualTo(month.plusMonths(1).atDay(10));
    }

    @Test
    @DisplayName("Should create invoice as unpaid when current date is before closing date")
    void testCreateInvoiceAsUnpaidBeforeClosingDate() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      assertThat(invoice.getIsPaid()).isFalse();
    }

    @Test
    @DisplayName("Should create invoice as paid when current date is after closing date")
    void testCreateInvoiceAsPaidAfterClosingDate() {
      var clock = createClock();
      var month = YearMonth.now(clock).minusMonths(1); // Previous month

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      assertThat(invoice.getIsPaid()).isTrue();
    }

    @Test
    @DisplayName("Should return the given amount when invoice was manually added")
    void testSumExpensesWithManuallyAddedInvoice() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      // Manually setting the amount
      var amount = BigDecimal.valueOf(100.00);
      invoice.updateAmount(amount);

      assertThat(invoice.sumExpenses()).isEqualTo(amount);
    }

    @Test
    @DisplayName("Should return zero when expenses are marked as ignored")
    void testSumExpensesWithIgnoredExpenses() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      // Adding ignored expenses
      invoice.setExpenses(List.of(
        ExpenseEntity.builder().amount(BigDecimal.valueOf(50.00)).paymentType(PaymentTypeEnum.CREDIT).isIgnored(true).build(),
        ExpenseEntity.builder().amount(BigDecimal.valueOf(30.00)).paymentType(PaymentTypeEnum.CREDIT).isIgnored(true).build()
      ));

      assertThat(invoice.sumExpenses()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return zero when expenses are of type DEBIT")
    void testSumExpensesWithDebitExpenses() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      // Adding debit expenses
      invoice.setExpenses(List.of(
        ExpenseEntity.builder().amount(BigDecimal.valueOf(50.00)).paymentType(PaymentTypeEnum.DEBIT).isIgnored(false).build(),
        ExpenseEntity.builder().amount(BigDecimal.valueOf(30.00)).paymentType(PaymentTypeEnum.DEBIT).isIgnored(false).build()
      ));

      assertThat(invoice.sumExpenses()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should create unpaid invoice when current date is before closing date")
    void testCreateUnpaidInvoiceBeforeClosingDate() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = InvoiceEntity.create(card, month, clock);

      assertThat(invoice.getIsPaid()).isFalse();
    }

    @Test
    @DisplayName("Should create paid invoice when current date is after closing date")
    void testCreatePaidInvoiceAfterClosingDate() {
      var clock = createClock();
      var month = YearMonth.now(clock).minusMonths(1); // Previous month

      var card = createCard();
      var invoice = InvoiceEntity.create(card, month, clock);

      assertThat(invoice.getIsPaid()).isTrue();
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when setting negative invoice amount")
    void testSetInvoiceAmountToNegative() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      assertThatIllegalArgumentException()
        .isThrownBy(() -> invoice.updateAmount(BigDecimal.valueOf(-100.00)))
        .withMessage("'newAmount' must be greater than or equal to zero");
    }

    @Test
    @DisplayName("Should be able to set invoice as paid")
    void testSetInvoiceAsPaid() {
      var clock = createClock();
      var month = YearMonth.now(clock);

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      invoice.setAsPaid();

      assertThat(invoice.getIsPaid()).isTrue();
    }

    @Test
    @DisplayName("Should sum expenses for invoice")
    void testSumExpenses() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      // Assuming expenses are added to the invoice
      invoice.setExpenses(List.of(
        ExpenseEntity.builder().amount(BigDecimal.valueOf(50.00)).paymentType(PaymentTypeEnum.CREDIT).isIgnored(false).build(),
        ExpenseEntity.builder().amount(BigDecimal.valueOf(30.00)).paymentType(PaymentTypeEnum.CREDIT).isIgnored(false).build()
      ));

      assertThat(invoice.sumExpenses()).isEqualTo(BigDecimal.valueOf(80.00));
    }

    @Test
    @DisplayName("Should throw NullPointerException when setting invoice amount to null")
    void testSetInvoiceAmountToNull() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      assertThatNullPointerException()
        .isThrownBy(() -> invoice.updateAmount(null))
        .withMessage("'newAmount' cannot be null");
    }

    @Test
    @DisplayName("Should be able to manually set invoice amount")
    void testSetInvoiceAmount() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      var amount = BigDecimal.valueOf(100.00);
      invoice.updateAmount(amount);

      assertThat(invoice.getAmount()).isEqualTo(amount);
      assertThat(invoice.getWasManuallyAdded()).isTrue();
    }

    @Test
    @DisplayName("Should return zero when expenses are empty")
    void testSumExpensesWithNoExpenses() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      assertThat(invoice.sumExpenses()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return zero when expenses are null")
    void testSumExpensesWithNullExpenses() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = createInvoiceEntity(card, month, clock);

      invoice.setExpenses(null); // Setting expenses to null

      assertThat(invoice.sumExpenses()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should create invoice with zero amount")
    void testCreateInvoiceWithZeroAmount() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      var card = createCard();
      var invoice = InvoiceEntity.create(card, month, clock);

      assertThat(invoice.getAmount()).isEqualTo(BigDecimal.ZERO);
    }
  }

  @Nested
  @DisplayName("")
  class InvoiceEntityNonFunctionalTests {
    @Test
    @DisplayName("Should throw NullPointerException when card is null")
    void testCreateInvoiceWithNullCard() {
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      assertThatNullPointerException()
        .isThrownBy(() -> InvoiceEntity.create(null, month, clock))
        .withMessage("'card' cannot be null");
    }

    @Test
    @DisplayName("Should throw NullPointerException when month is null")
    void testCreateInvoiceWithNullMonth() {
      var clock = createClock();
      var card = createCard();

      assertThatNullPointerException()
        .isThrownBy(() -> InvoiceEntity.create(card, null, clock))
        .withMessage("'yearMonth' cannot be null");
    }

    @Test
    @DisplayName("Should throw NullPointerException when clock is null")
    void testCreateInvoiceWithNullClock() {
      var card = createCard();
      var clock = createClock();
      var month = YearMonth.now(clock); // Current month

      assertThatNullPointerException()
        .isThrownBy(() -> InvoiceEntity.create(card, month, null))
        .withMessage("'clock' cannot be null");
    }
  }

  private Clock createClock() {
    var timeZone = Clock.systemDefaultZone().getZone();
    var instant = CURRENT_DATE_TIME.atZone(timeZone).toInstant();

    return Clock.fixed(instant, timeZone);
  }

  private InvoiceEntity createInvoiceEntity(CardEntity card, YearMonth month, Clock clock) {
    return InvoiceEntity.create(card, month, clock);
  }

  private CardEntity createCard() {
    return CardEntity.builder()
      .id(1L)
      .name("Valid card name")
      .closingDay(CLOSING_DAY)
      .paymentDay(PAYMENT_DAY)
      .build();
  }
}
