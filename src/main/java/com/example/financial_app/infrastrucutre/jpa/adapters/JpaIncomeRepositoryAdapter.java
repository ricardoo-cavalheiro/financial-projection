package com.example.financial_app.infrastrucutre.jpa.adapters;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.financial_app.domain.dao.IIncomeRepository;
import com.example.financial_app.domain.entities.IncomeEntity;
import com.example.financial_app.infrastrucutre.jpa.entities.JpaIncomeEntity;
import com.example.financial_app.infrastrucutre.jpa.ports.JpaIncomeRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaIncomeRepositoryAdapter implements IIncomeRepository {
  private final ModelMapper modelMapper;
  private final JpaIncomeRepositoryPort jpaIncomeRepositoryPort;

  @Override
  public IncomeEntity save(IncomeEntity income) {
    var incomeJpaEntity = modelMapper.map(income, JpaIncomeEntity.class);
    var savedIncomeJpaEntity = jpaIncomeRepositoryPort.save(incomeJpaEntity);

    return modelMapper.map(savedIncomeJpaEntity, IncomeEntity.class);
  }

  @Override
  public Optional<IncomeEntity> findById(Long id) {
    var incomeJpaEntity = jpaIncomeRepositoryPort.findById(id);

    if (incomeJpaEntity.isPresent()) {
      return Optional.of(modelMapper.map(incomeJpaEntity.get(), IncomeEntity.class));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public List<IncomeEntity> findAllByPaymentDateBetween(LocalDate startDate, LocalDate endDate) {
    var incomeJpaEntities = jpaIncomeRepositoryPort.findAllByPaymentDateBetween(startDate, endDate);

    return incomeJpaEntities
      .stream()
      .map(entity -> modelMapper.map(entity, IncomeEntity.class))
      .toList();
  }

  @Override
  public List<IncomeEntity> saveAll(List<IncomeEntity> incomes) {
    var incomeJpaEntities = incomes
      .stream()
      .map(income -> modelMapper.map(income, JpaIncomeEntity.class))
      .toList();

    var savedIncomeJpaEntities = Arrays.asList(jpaIncomeRepositoryPort.saveAll(incomeJpaEntities));

    return savedIncomeJpaEntities
      .stream()
      .map(entity -> modelMapper.map(entity, IncomeEntity.class))
      .toList();
  }

  @Override
  public List<IncomeEntity> findAll() {
    var incomeJpaEntities = jpaIncomeRepositoryPort.findAll();

    return incomeJpaEntities
      .stream()
      .map(entity -> modelMapper.map(entity, IncomeEntity.class))
      .toList();
  }
}
