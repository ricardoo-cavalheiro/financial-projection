package com.example.financial_app.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.financial_app.domain.entities.CardEntity;

@Repository
public interface ICardRepository extends CrudRepository<CardEntity, String> {
  Optional<CardEntity> findByName(String name);
}
