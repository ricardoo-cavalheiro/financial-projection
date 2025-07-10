package com.example.financial_app.domain.services;

import java.math.BigDecimal;
import java.util.List;

import com.example.financial_app.domain.entities.CardEntity;

public interface ICardService {
  public void createCard(
    String cardName,
    BigDecimal limitAmount,
    Integer closingDay,
    Integer paymentDay
  );

  public CardEntity getCard(String cardName);

  public List<CardEntity> getAllCards();
}
