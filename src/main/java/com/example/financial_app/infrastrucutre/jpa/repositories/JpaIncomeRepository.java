package com.example.financial_app.infrastrucutre.jpa.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.financial_app.domain.dao.IIncomeRepository;
import com.example.financial_app.domain.entities.IncomeEntity;

@Repository
public interface JpaIncomeRepository extends CrudRepository<IncomeEntity, Long>, IIncomeRepository {
  @Query("SELECT i FROM IncomeEntity i WHERE i.paymentDate >= :startDate AND i.paymentDate <= :endDate ORDER BY i.paymentDate ASC")
  List<IncomeEntity> findAllByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
}
