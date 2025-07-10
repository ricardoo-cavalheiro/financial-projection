package com.example.financial_app.infrastrucutre.jpa.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.example.financial_app.domain.dao.IInvoiceRepository;
import com.example.financial_app.domain.entities.InvoiceEntity;

@Repository
public interface JpaInvoiceRepository extends CrudRepository<InvoiceEntity, Long>, IInvoiceRepository {
  @Override
  @Query("SELECT i FROM InvoiceEntity i WHERE i.card.name = :cardName AND i.closingDate >= :startDate")
  List<InvoiceEntity> findAllByClosingDateAndCardName(LocalDate startDate, String cardName, Limit limit, Sort sort);

  @Override
  @Query("SELECT i FROM InvoiceEntity i WHERE i.card.name = :cardName AND i.closingDate = :closingDate")
  Optional<InvoiceEntity> findByClosingDateAndCardName(LocalDate closingDate, String cardName);

  @Override
  @NonNull
  Optional<InvoiceEntity> findById(@NonNull Long id);
}
