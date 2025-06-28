package com.example.financial_app.services;

import org.springframework.shell.command.annotation.Command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Command(group = "Projection", description = "Commands related to financial projections.")
@RequiredArgsConstructor
public class ProjectionService {
  private final CardService cardService;
  private final InvoiceService invoiceService;

  @Command(command = "expenses-projection", description = "Project expenses for the next 12 months.")
  public void expensesProjection() {
    var cards = cardService.getAllCards();

    log.info("Found {} cards for projection", cards.size());

    for (var card : cards) {
      log.info("Card: {}", card.getName());

      var invoices = invoiceService.getInvoices(card.getName());
      for (var invoice : invoices) {
        var invoiceTotalAmount = invoiceService.sumInvoiceExpenses(invoice);

        log.info("{}: {}", invoice.getClosingDate(), invoiceTotalAmount);
      }
    }
  }
}
