package com.example.financial_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import com.example.financial_app.domain.dao.IInvoiceRepository;
import com.example.financial_app.domain.entities.InvoiceEntity;
import com.example.financial_app.domain.services.ICardService;
import com.example.financial_app.domain.services.IInvoiceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Command(group = "Invoice", description = "Commands related to invoice management.")
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceService {
  private final IInvoiceRepository invoiceRepository;
  private final ICardService cardService;

  @Command(command = "create-invoices", description = "Create a new invoice.")
  public void createInvoice(
      @Option(required = true) Integer months,
      @Option(required = true) String cardName) {
    log.info("Creating {} invoices", months);

    var card = cardService.getCard(cardName);

    var invoices = new ArrayList<InvoiceEntity>();
    for (int i = 1; i <= months; i++) {
      var invoice = InvoiceEntity.create(card, Month.of(i));

      invoices.add(invoice);
      log.info("Invoice created with closing date: {}", invoice.getClosingDate());
    }

    invoiceRepository.saveAll(invoices);
  }

  @Command(command = "set-invoice-paid", description = "Set an invoice as paid.")
  public void setInvoicePaid(
      @Option(required = true) String cardName,
      @Option(required = true) LocalDate closingDate,
      @Option(required = true) Boolean isPaid) {
    log.info("Setting invoice closing date {} as paid: {}", closingDate, isPaid);

    var card = cardService.getCard(cardName);

    var invoice = invoiceRepository.findByClosingDateAndCardName(closingDate, card.getName())
        .orElseThrow(() -> new IllegalArgumentException("Invoice not found with closing date: " + closingDate));

    invoice.setAsPaid(isPaid);
    invoiceRepository.save(invoice);

    log.info("Invoice with closing date {} updated successfully. Is Paid: {}", closingDate, isPaid);
  }

  public List<InvoiceEntity> getInvoices(String cardName, Integer months) {
    log.info("Retrieving invoices for the next 12 months.");

    var currentDate = LocalDate.now().withDayOfMonth(1);
    var invoices = invoiceRepository.findAllByClosingDateAndCardName(
        currentDate,
        cardName,
        Limit.of(months),
        Sort.by(Sort.Direction.ASC, "closingDate"));

    if (invoices.isEmpty()) {
      log.info("No invoices found.");
    } else {
      invoices.forEach(invoice -> log.info(
          "Invoice ID: {}, Card: {}, Amount: {}, Closing Date: {}, Payment Date: {}, Is Paid: {}",
          invoice.getId(), invoice.getCard().getName(), invoice.getAmount(),
          invoice.getClosingDate(), invoice.getPaymentDate(), invoice.getIsPaid()));
    }

    return invoices;
  }

  public void replaceInvoiceAmountById(String id, BigDecimal amount) {
    log.info("Replacing invoice amount for ID: {}", id);

    var invoice = invoiceRepository.findById(Long.parseLong(id))
        .orElseThrow(() -> new IllegalArgumentException("Invoice not found with ID: " + id));

    invoice.replaceInvoiceAmountById(amount);
    invoiceRepository.save(invoice);

    log.info("Invoice amount updated successfully for ID: {}", id);
  }

  public InvoiceEntity getInvoiceByClosingDateAndCardName(LocalDate closingDate, String cardName) {
    log.info("Retrieving invoice for closing date '{}'", closingDate);

    var invoice = invoiceRepository.findByClosingDateAndCardName(closingDate, cardName).orElseThrow(
        () -> new IllegalArgumentException("Invoice not found with closing date: " + closingDate));

    return invoice;
  }

  public BigDecimal sumInvoiceExpenses(InvoiceEntity invoice) {
    log.info("Summing expenses for card '{}'", invoice.getCard().getName());

    var totalAmount = invoice.sumExpenses();
    invoice.setAmount(totalAmount);
    invoiceRepository.save(invoice);

    log.info("Total amount for invoice ID {}: {}", invoice.getId(), totalAmount);
    return totalAmount;
  }
}
