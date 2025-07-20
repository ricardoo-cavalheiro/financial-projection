package com.example.financial_app.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import com.example.financial_app.domain.services.IInvoiceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Command(group = "Invoice", description = "Commands related to invoice management.")
@RequiredArgsConstructor
public class InvoiceController {
  private final IInvoiceService invoiceService;

  @Command(command = "create-invoices", description = "Create a new invoice.")
  public void createInvoice(
    @Option(required = true) Integer months,
    @Option(required = true) String cardName
  ) {
    invoiceService.createInvoice(months, cardName);
  }

  @Command(command = "set-invoice-paid", description = "Set an invoice as paid.")
  public void setInvoicePaid(
    @Option(required = true) String cardName,
    @Option(required = true) LocalDate closingDate,
    @Option(required = true) Boolean isPaid
  ) {
    invoiceService.setInvoicePaid(cardName, closingDate, isPaid);
  }

  @Command(command = "update-invoice-amount", description = "Update the amount of an invoice.")
  public void updateInvoiceAmount(
    @Option(required = true) String invoiceId,
    @Option(required = true) BigDecimal amount
  ) {
    invoiceService.updateInvoiceAmountById(invoiceId, amount);
  }
}
