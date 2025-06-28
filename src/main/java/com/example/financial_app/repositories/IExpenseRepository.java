package com.example.financial_app.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.financial_app.domain.entities.ExpenseEntity;

@Repository
public interface IExpenseRepository extends CrudRepository<ExpenseEntity, Long> {
  @Query("SELECT e FROM ExpenseEntity e WHERE e.paymentType = 'DEBIT' AND e.paymentDate >= :startDate")
  List<ExpenseEntity> findDebitExpenses(LocalDate startDate, Limit limit, Sort sort);
}
