package com.example.financial_app.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import com.example.financial_app.domain.entities.CardEntity;
import com.example.financial_app.domain.services.ICardService;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Command(group = "Card", description = "Commands related to card management")
@RequiredArgsConstructor
public class CardController {
  private final ICardService cardService;

  @Command(command = "create-card", description = "Create a new card")
  public void createCard(
    @Option(required = true) @Size(min=3, max=20, message = "Invalid card name. Name must be between 3 and 20 characters.") String cardName,
    @Option(required = true) @Min(0) BigDecimal limitAmount,
    @Option(required = true) @Min(1) Integer closingDay,
    @Option(required = true) @Min(1) Integer paymentDay
  ) {
    cardService.createCard(cardName, limitAmount, closingDay, paymentDay);
  }

  @Command(command = "get-card", description = "Get card details")
  public CardEntity getCard(@Option(required = true) String cardName) {
    return cardService.getCard(cardName);
  }

  @Command(command = "get-all-cards", description = "Get all cards")
  public List<CardEntity> getAllCards() {
    return cardService.getAllCards();
  }
}
