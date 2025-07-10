package com.example.financial_app.domain.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.financial_app.domain.entities.InvoiceEntity;

public interface IInvoiceService {
  public void createInvoice(
    Integer months,
    String cardName
  );

  public void setInvoicePaid(
    String cardName,
    LocalDate closingDate,
    Boolean isPaid
  );

  public List<InvoiceEntity> getInvoices(String cardName, Integer months);

  public void replaceInvoiceAmountById(String id, BigDecimal amount);

  public InvoiceEntity getInvoiceByClosingDateAndCardName(LocalDate closingDate, String cardName);

  public BigDecimal sumInvoiceExpenses(InvoiceEntity invoice);
}
