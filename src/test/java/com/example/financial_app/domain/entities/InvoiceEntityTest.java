package com.example.financial_app.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

public class InvoiceEntityTest {
  private final static Integer CLOSING_DAY = 15;
  private final static Integer PAYMENT_DAY = 20;
  private final static LocalDateTime CURRENT_DATE_TIME = LocalDateTime.now();

  @Test
  @DisplayName("Should create invoice with valid card and month")
  void testCreateInvoice() {
    var clock = createClock();
    var currentDate = CURRENT_DATE_TIME.toLocalDate();

    var card = createCard();
    var month = 0; // Current month

    var invoice = InvoiceEntity.create(card, month, clock);

    assertThat(invoice.getCard())
      .as("Invoice was set to card '%s'", invoice.getCard().getName())
      .isEqualTo(card);
    assertThat(invoice.getClosingDate()).isEqualTo(currentDate.withDayOfMonth(CLOSING_DAY));
    assertThat(invoice.getPaymentDate()).isEqualTo(currentDate.withDayOfMonth(PAYMENT_DAY));
    assertThat(invoice.getAmount()).isEqualTo(BigDecimal.ZERO);
    assertThat(invoice.getWasManuallyAdded()).isFalse();
    assertThat(invoice.getCreatedAt()).isEqualTo(CURRENT_DATE_TIME);
  }

  private Clock createClock() {
    var timeZone = Clock.systemDefaultZone().getZone();
    var instant = CURRENT_DATE_TIME.atZone(timeZone).toInstant();

    return Clock.fixed(instant, timeZone);
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
