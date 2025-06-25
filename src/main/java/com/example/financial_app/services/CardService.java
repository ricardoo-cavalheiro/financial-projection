package com.example.financial_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.transaction.annotation.Transactional;

import com.example.financial_app.domain.entities.CardEntity;
import com.example.financial_app.domain.entities.InvoiceEntity;
import com.example.financial_app.repositories.ICardRepository;
import com.example.financial_app.repositories.IInvoiceRepository;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Command(group = "Card", description = "Commands related to card management")
@RequiredArgsConstructor
public class CardService {
  private final IInvoiceRepository invoiceRepository;
  private final ICardRepository cardRepository;

  @Transactional
  @Command(command = "create-card", description = "Create a new card")
  public void run (
    @Option(required = true) @Size(min=3, max=20, message = "Invalid card name. Name must be between 3 and 20 characters.") String cardName,
    @Option(required = true) @Min(0) BigDecimal limitAmount,
    @Option(required = true) @Min(1) Integer closingDay,
    @Option(required = true) @Min(1) Integer paymentDay
  ) {
    log.info(
      "Creating card with name: {}, limit: {}, closing day: {}, payment day: {}",
      cardName, limitAmount, closingDay, paymentDay
    );

    var card = CardEntity.builder()
      .name(cardName)
      .limitAmount(limitAmount)
      .paymentDay(paymentDay)
      .closingDay(closingDay)
      .build();

    var currentDate = LocalDate.now();
    var closingDate = currentDate.withDayOfMonth(closingDay);
    var paymentDate = currentDate.withDayOfMonth(paymentDay);
    var invoice = InvoiceEntity.builder()
      .card(card)
      .isPaid(Boolean.FALSE)
      .amount(BigDecimal.ZERO)
      .closingDate(closingDate)
      .paymentDate(paymentDate)
      .isPaid(currentDate.isAfter(paymentDate))
      .build();

    cardRepository.save(card);
    invoiceRepository.save(invoice);

    log.info("Card created successfully!");
  }
}
