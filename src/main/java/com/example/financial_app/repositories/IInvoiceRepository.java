package com.example.financial_app.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.financial_app.domain.entities.InvoiceEntity;

@Repository
public interface IInvoiceRepository extends CrudRepository<InvoiceEntity, Long> {}
