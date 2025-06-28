package com.example.financial_app.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.financial_app.domain.entities.InvoiceEntity;

@Repository
public interface IInvoiceRepository extends CrudRepository<InvoiceEntity, Long> {
  @Query("SELECT i FROM InvoiceEntity i WHERE i.card.name = :cardName AND i.closingDate >= :startDate")
  List<InvoiceEntity> findAllByCardName(LocalDate startDate, String cardName, Limit limit, Sort sort);

  Optional<InvoiceEntity> findByClosingDate(LocalDate closingDate);
}
