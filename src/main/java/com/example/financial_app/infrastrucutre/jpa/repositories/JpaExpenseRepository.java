package com.example.financial_app.infrastrucutre.jpa.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.financial_app.domain.dao.IExpenseRepository;
import com.example.financial_app.domain.entities.ExpenseEntity;

@Repository
public interface JpaExpenseRepository extends CrudRepository<ExpenseEntity, Long>, IExpenseRepository {
  @Query("""
    SELECT e FROM ExpenseEntity e 
    WHERE e.paymentType = 'DEBIT' 
      AND e.paymentDate >= :startDate
      AND e.description LIKE %:description%""")
  List<ExpenseEntity> findAllDebitExpensesByDescription(LocalDate startDate, String description, Limit limit, Sort sort);

  @Query("""
    SELECT DISTINCT (e.description) 
    FROM ExpenseEntity e 
    WHERE e.paymentType = 'DEBIT'
      AND e.paymentDate >= :startDate
      AND e.paymentDate <= :endDate""")
  List<String> findAllDebitExpenses(LocalDate startDate, LocalDate endDate);

  @Query("""
    SELECT e FROM ExpenseEntity e 
    WHERE e.paymentType = 'DEBIT' 
      AND e.paymentDate >= :startDate
      AND e.paymentDate <= :endDate""")
  List<ExpenseEntity> findAllDebitExpensesByYearMonth(LocalDate startDate, LocalDate endDate);
}
