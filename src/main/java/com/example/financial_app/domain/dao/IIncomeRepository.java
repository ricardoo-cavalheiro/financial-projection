package com.example.financial_app.domain.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.financial_app.domain.entities.IncomeEntity;

public interface IIncomeRepository {
  List<IncomeEntity> findAllByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

  List<IncomeEntity> saveAll(List<IncomeEntity> incomes);

  IncomeEntity save(IncomeEntity income);

  List<IncomeEntity> findAll();

  public Optional<IncomeEntity> findById(Long id);
}
