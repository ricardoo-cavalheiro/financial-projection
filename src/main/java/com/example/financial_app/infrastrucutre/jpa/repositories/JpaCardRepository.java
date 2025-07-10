package com.example.financial_app.infrastrucutre.jpa.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import com.example.financial_app.domain.dao.ICardRepository;
import com.example.financial_app.domain.entities.CardEntity;

@Repository
public interface JpaCardRepository extends CrudRepository<CardEntity, Long>, ICardRepository {
  @Override
  public Optional<CardEntity> findByName(String name);
}
