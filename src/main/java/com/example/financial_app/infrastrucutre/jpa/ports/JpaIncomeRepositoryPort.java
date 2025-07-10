package com.example.financial_app.infrastrucutre.jpa.ports;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.financial_app.infrastrucutre.jpa.entities.JpaIncomeEntity;

@Repository
public interface JpaIncomeRepositoryPort extends JpaRepository<JpaIncomeEntity, Long> {
  @Query("SELECT i FROM IncomeEntity i WHERE i.paymentDate >= :startDate AND i.paymentDate <= :endDate ORDER BY i.paymentDate ASC")
  List<JpaIncomeEntity> findAllByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
}
