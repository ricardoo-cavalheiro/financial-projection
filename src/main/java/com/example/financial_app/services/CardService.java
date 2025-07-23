package com.example.financial_app.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.financial_app.domain.dao.ICardRepository;
import com.example.financial_app.domain.entities.CardEntity;
import com.example.financial_app.domain.services.ICardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CardService implements ICardService {
  private final ICardRepository cardRepository;

  public void createCard(
    String cardName,
    BigDecimal limitAmount,
    Integer closingDay,
    Integer paymentDay
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

  public CardEntity getCard(String cardName) {
    log.info("Retrieving card with name: {}", cardName);

    var card = cardRepository.findByName(cardName)
      .orElseThrow(() -> new IllegalArgumentException("Card not found"));

    log.info("Card details: Name: {}, Limit: {}, Closing Day: {}, Payment Day: {}",
      card.getName(), card.getLimitAmount(), card.getClosingDay(), card.getPaymentDay());

    return card;
  }

  public List<CardEntity> getAllCards() {
    log.info("Retrieving all cards");

    var cards = cardRepository.findAll();

    if (cards.isEmpty()) {
      log.info("No cards found");
    } else {
      cards.forEach(card -> log.info(card.toString()));
    }

    return cards;
  }
}
