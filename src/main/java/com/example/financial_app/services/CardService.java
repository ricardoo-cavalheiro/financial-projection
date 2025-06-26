package com.example.financial_app.services;

import java.math.BigDecimal;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.transaction.annotation.Transactional;

import com.example.financial_app.domain.entities.CardEntity;
import com.example.financial_app.repositories.ICardRepository;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Command(group = "Card", description = "Commands related to card management")
@RequiredArgsConstructor
public class CardService {
  private final ICardRepository cardRepository;

  @Command(command = "create-card", description = "Create a new card")
  public void createCard(
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

    cardRepository.save(card);

    log.info("Card created successfully!");
  }

  @Command(command = "get-card", description = "Get card details")
  public CardEntity getCard(@Option(required = true) String cardName) {
    log.info("Retrieving card with name: {}", cardName);

    var card = cardRepository.findByName(cardName)
      .orElseThrow(() -> new IllegalArgumentException("Card not found"));

    log.info("Card details: Name: {}, Limit: {}, Closing Day: {}, Payment Day: {}",
      card.getName(), card.getLimitAmount(), card.getClosingDay(), card.getPaymentDay());

    return card;
  }
}
