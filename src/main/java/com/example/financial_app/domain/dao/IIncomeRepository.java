package com.example.financial_app.domain.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.financial_app.domain.entities.IncomeEntity;

@Repository
public interface IIncomeRepository {
  List<IncomeEntity> findAllByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

  List<IncomeEntity> saveAll(List<IncomeEntity> incomes);

  IncomeEntity save(IncomeEntity income);

  List<IncomeEntity> findAll();
}
