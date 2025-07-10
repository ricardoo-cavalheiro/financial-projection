package com.example.financial_app.infrastrucutre.jpa.adapters;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.financial_app.domain.dao.ICardRepository;
import com.example.financial_app.domain.entities.CardEntity;
import com.example.financial_app.infrastrucutre.jpa.entities.JpaCardEntity;
import com.example.financial_app.infrastrucutre.jpa.ports.JpaCardRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaCardRepositoryAdapter implements ICardRepository {
  private final ModelMapper modelMapper;
  private final JpaCardRepositoryPort jpaCardRepositoryPort;

  @Override
  public CardEntity save(CardEntity card) {
    var cardJpaEntity = modelMapper.map(card, JpaCardEntity.class);
    var savedCardJpaEntity = jpaCardRepositoryPort.save(cardJpaEntity);

    return modelMapper.map(savedCardJpaEntity, CardEntity.class);
  }

  @Override
  public List<CardEntity> findAll() {
    var cardJpaEntities = jpaCardRepositoryPort.findAll();

    return cardJpaEntities
      .stream()
      .map(entity -> modelMapper.map(jpaCardRepositoryPort, CardEntity.class))
      .toList();
  }

  @Override
  public Optional<CardEntity> findByName(String name) {
    var cardJpaEntity = jpaCardRepositoryPort.findByName(name);

    if (cardJpaEntity.isPresent()) {
      return Optional.of(modelMapper.map(cardJpaEntity.get(), CardEntity.class));
    } else {
      return Optional.empty();
    }
  }
}
