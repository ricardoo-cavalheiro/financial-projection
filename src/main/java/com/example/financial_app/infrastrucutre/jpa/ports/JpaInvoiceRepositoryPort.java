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

import com.example.financial_app.infrastrucutre.jpa.entities.JpaInvoiceEntity;

@Repository
public interface JpaInvoiceRepositoryPort extends CrudRepository<JpaInvoiceEntity, Long> {
  @Query("SELECT i FROM InvoiceEntity i WHERE i.card.name = :cardName AND i.closingDate >= :startDate")
  List<JpaInvoiceEntity> findAllByClosingDateAndCardName(LocalDate startDate, String cardName, Limit limit, Sort sort);

  @Query("SELECT i FROM InvoiceEntity i WHERE i.card.name = :cardName AND i.closingDate = :closingDate")
  Optional<JpaInvoiceEntity> findByClosingDateAndCardName(LocalDate closingDate, String cardName);

  @NonNull List<JpaInvoiceEntity> findAll();
}
