package com.example.financial_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import com.example.financial_app.domain.entities.InvoiceEntity;
import com.example.financial_app.repositories.IInvoiceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Command(group = "Invoice", description = "Commands related to invoice management.")
@RequiredArgsConstructor
public class InvoiceService {
    private final IInvoiceRepository invoiceRepository;
    private final CardService cardService;

    @Command(command = "create-invoices", description = "Create a new invoice.")
    public void createInvoice(
      @Option(required = true) Integer months, 
      @Option(required = true) String cardName
    ) {
        log.info("Creating {} invoices", months);

        var card = cardService.getCard(cardName);
        var currentDate = LocalDate.now();

        var invoices = new ArrayList<InvoiceEntity>();
        for (int i = 0; i < months; i++) {
          var closingDate = currentDate.withDayOfMonth(card.getClosingDay()).plusMonths(i);
          var paymentDate = currentDate.withDayOfMonth(card.getPaymentDay()).plusMonths(i);

          var invoice = InvoiceEntity.builder()
              .card(card)
              .amount(BigDecimal.ZERO)
              .isPaid(currentDate.isAfter(closingDate))
              .closingDate(closingDate)
              .paymentDate(paymentDate)
              .build();

          invoices.add(invoice);
          log.info("Invoice created with closing date: {}", invoice.getClosingDate());
        }

        invoiceRepository.saveAll(invoices);
    }

    @Command(command = "get-invoices", description = "Get next 12 months invoices.")
    public List<InvoiceEntity> getInvoices(@Option(required = false) String cardName) {
        log.info("Retrieving invoices for the next 12 months.");

        var invoices = invoiceRepository.findAllByCardName(cardName, Limit.of(12));

        if (invoices.isEmpty()) {
            log.info("No invoices found.");
        } else {
            invoices.forEach(invoice -> log.info(
                "Invoice ID: {}, Card: {}, Amount: {}, Closing Date: {}, Payment Date: {}, Is Paid: {}",
                invoice.getId(), invoice.getCard().getName(), invoice.getAmount(),
                invoice.getClosingDate(), invoice.getPaymentDate(), invoice.getIsPaid()
            ));
        }

        return invoices;
    }

    @Command(command = "get-current-month-invoice", description = "Get card invoice for the current month.")
    public InvoiceEntity getCurrentMonthInvoice(@Option(required = true) String cardName) {
      log.info("Retrieving current month invoice for card: {}", cardName);

      var currentDate = LocalDate.now();
      var invoice = getInvoices(cardName).stream()
          .filter(x -> currentDate.isBefore(x.getClosingDate()) || currentDate.isEqual(x.getClosingDate()))
          .findFirst();

      if (invoice.isEmpty()) {
        log.info("No invoice found for the current month.");
      }

      log.info("Current month invoice found: {}", invoice.get());
      return invoice.get();
    }
}
