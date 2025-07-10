package com.example.financial_app.domain.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.example.financial_app.domain.entities.ExpenseEntity;

@Repository
public interface IExpenseRepository {
  List<ExpenseEntity> findAllDebitExpensesByDescription(LocalDate startDate, String description, Limit limit, Sort sort);

  List<String> findAllDebitExpenses(LocalDate startDate, LocalDate endDate);

  List<ExpenseEntity> findAllDebitExpensesByYearMonth(LocalDate startDate, LocalDate endDate);

  List<ExpenseEntity> saveAll(List<ExpenseEntity> expenses);

  ExpenseEntity save(ExpenseEntity expense);
}
