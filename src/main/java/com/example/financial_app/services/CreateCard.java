package com.example.financial_app.services;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellOption;

import com.example.financial_app.domain.entities.CardEntity;
import com.example.financial_app.repositories.ICardRepository;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Command(group = "Card", description = "Commands related to card management")
@RequiredArgsConstructor
public class CreateCard {
  private final ICardRepository cardRepository;

  @Command(command = "create-card", description = "Create a new card")
  public void run (
    @Option(required = true) @Size(min=3, max=20, message = "Invalid card name. Name must be between 3 and 20 characters.") String cardName,
    @Option(required = true) @Min(0) Double limitAmount,
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
      .closingDay(closingDay)
      .paymentDay(paymentDay)
      .build();

    cardRepository.save(card);

    log.info("Card created successfully!");
  }
}
