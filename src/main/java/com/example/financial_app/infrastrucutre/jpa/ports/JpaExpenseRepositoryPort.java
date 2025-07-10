package com.example.financial_app.infrastrucutre.jpa.ports;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.example.financial_app.infrastrucutre.jpa.entities.JpaExpenseEntity;

@Repository
public interface JpaExpenseRepositoryPort extends CrudRepository<JpaExpenseEntity, Long> {
  @Query("""
    SELECT e FROM ExpenseEntity e 
    WHERE e.paymentType = 'DEBIT' 
      AND e.paymentDate >= :startDate
      AND e.description LIKE %:description%""")
  List<JpaExpenseEntity> findAllDebitExpensesByDescription(LocalDate startDate, String description, Limit limit, Sort sort);

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
  List<JpaExpenseEntity> findAllDebitExpensesByYearMonth(LocalDate startDate, LocalDate endDate);

  @NonNull List<JpaExpenseEntity> findAll();

  @NonNull Optional<JpaExpenseEntity> findById(@NonNull Long id);
}
