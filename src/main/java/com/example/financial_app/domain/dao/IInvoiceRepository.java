package com.example.financial_app.domain.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import com.example.financial_app.domain.entities.InvoiceEntity;

public interface IInvoiceRepository {
  List<InvoiceEntity> findAllByClosingDateAndCardName(LocalDate startDate, String cardName, Limit limit, Sort sort);

  Optional<InvoiceEntity> findByClosingDateAndCardName(LocalDate closingDate, String cardName);

  Optional<InvoiceEntity> findById(@NonNull Long id);

  List<InvoiceEntity> saveAll(List<InvoiceEntity> invoices);

  InvoiceEntity save(InvoiceEntity invoice);
}
