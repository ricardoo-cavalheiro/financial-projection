package com.example.financial_app.infrastrucutre.jpa.adapters;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.example.financial_app.domain.dao.IInvoiceRepository;
import com.example.financial_app.domain.entities.InvoiceEntity;
import com.example.financial_app.infrastrucutre.jpa.entities.JpaInvoiceEntity;
import com.example.financial_app.infrastrucutre.jpa.ports.JpaInvoiceRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaInvoiceRepositoryAdapter implements IInvoiceRepository {
  private final ModelMapper modelMapper;
  private final JpaInvoiceRepositoryPort jpaInvoiceRepositoryPort;

  public List<InvoiceEntity> findAllByClosingDateAndCardName(LocalDate startDate, String cardName, Limit limit, Sort sort) {
    var invoiceJpaEntities = jpaInvoiceRepositoryPort.findAllByClosingDateAndCardName(startDate, cardName, limit, sort);
    return invoiceJpaEntities.stream()
        .map(entity -> modelMapper.map(entity, InvoiceEntity.class))
        .toList();
  }

  public Optional<InvoiceEntity> findByClosingDateAndCardName(LocalDate closingDate, String cardName) {
    var invoiceJpaEntity = jpaInvoiceRepositoryPort.findByClosingDateAndCardName(closingDate, cardName);

    if (invoiceJpaEntity.isPresent()) {
      return Optional.of(modelMapper.map(invoiceJpaEntity.get(), InvoiceEntity.class));
    } else {
      return Optional.empty();
    }
  }

  public Optional<InvoiceEntity> findById(@NonNull Long id) {
    var invoiceJpaEntity = jpaInvoiceRepositoryPort.findById(id);

    if (invoiceJpaEntity.isPresent()) {
      return Optional.of(modelMapper.map(invoiceJpaEntity.get(), InvoiceEntity.class));
    } else {
      return Optional.empty();
    }
  }

  public List<InvoiceEntity> saveAll(List<InvoiceEntity> invoices) {
    var invoiceJpaEntities = invoices.stream()
        .map(invoice -> modelMapper.map(invoice, JpaInvoiceEntity.class))
        .toList();
    var savedInvoiceJpaEntities = Arrays.asList(jpaInvoiceRepositoryPort.saveAll(invoiceJpaEntities));

    return savedInvoiceJpaEntities
        .stream()
        .map(entity -> modelMapper.map(entity, InvoiceEntity.class))
        .toList();
  }

  public InvoiceEntity save(InvoiceEntity invoice) {
    var invoiceJpaEntity = modelMapper.map(invoice, JpaInvoiceEntity.class);
    var savedInvoiceJpaEntity = jpaInvoiceRepositoryPort.save(invoiceJpaEntity);

    return modelMapper.map(savedInvoiceJpaEntity, InvoiceEntity.class);
  }
}
