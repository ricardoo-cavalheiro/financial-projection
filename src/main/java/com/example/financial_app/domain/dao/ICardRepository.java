package com.example.financial_app.domain.dao;

import java.util.List;
import java.util.Optional;

import com.example.financial_app.domain.entities.CardEntity;

public interface ICardRepository {
  CardEntity save(CardEntity card);

  List<CardEntity> findAll();

  Optional<CardEntity> findByName(String name);
}
