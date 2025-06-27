package com.example.financial_app.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.financial_app.domain.entities.InvoiceEntity;

@Repository
public interface IInvoiceRepository extends CrudRepository<InvoiceEntity, Long> {
  List<InvoiceEntity> findAllByCardName(String cardName, Limit limit);

  Optional<InvoiceEntity> findByClosingDate(LocalDate closingDate);
}
