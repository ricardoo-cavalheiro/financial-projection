package com.example.financial_app.domain.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;

import com.example.financial_app.domain.entities.ExpenseEntity;

public interface IExpenseRepository {
  List<ExpenseEntity> findAllDebitExpensesByDescription(LocalDate startDate, String description, Limit limit, Sort sort);

  List<String> findAllDebitExpenses(LocalDate startDate, LocalDate endDate);

  List<ExpenseEntity> findAllDebitExpensesByYearMonth(LocalDate startDate, LocalDate endDate);

  List<ExpenseEntity> saveAll(List<ExpenseEntity> expenses);

  ExpenseEntity save(ExpenseEntity expense);

  List<ExpenseEntity> findAll();

  Optional<ExpenseEntity> findById(Long id);
}
