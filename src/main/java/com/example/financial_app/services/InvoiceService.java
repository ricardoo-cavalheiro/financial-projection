package com.example.financial_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import com.example.financial_app.domain.entities.ExpenseEntity;
import com.example.financial_app.domain.entities.InvoiceEntity;
import com.example.financial_app.domain.enums.PaymentTypeEnum;
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
              .wasManuallyAdded(Boolean.FALSE)
              .build();

          invoices.add(invoice);
          log.info("Invoice created with closing date: {}", invoice.getClosingDate());
        }

        invoiceRepository.saveAll(invoices);
    }

    public List<InvoiceEntity> getInvoices(String cardName, Integer months) {
        log.info("Retrieving invoices for the next 12 months.");

        var currentDate = LocalDate.now().withDayOfMonth(1);
        var invoices = invoiceRepository.findAllByClosingDateAndCardName(
          currentDate,
          cardName, 
          Limit.of(months),
          Sort.by(Sort.Direction.ASC, "closingDate")
        );

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

    public void replaceInvoiceAmountById(String id, BigDecimal amount) {
      log.info("Replacing invoice amount for ID: {}", id);

      var invoice = invoiceRepository.findById(Long.parseLong(id))
          .orElseThrow(() -> new IllegalArgumentException("Invoice not found with ID: " + id));

      invoice.setAmount(amount);
      invoice.setWasManuallyAdded(Boolean.TRUE);
      invoiceRepository.save(invoice);

      log.info("Invoice amount updated successfully for ID: {}", id);
    }

    public Optional<InvoiceEntity> getInvoiceByClosingDateAndCardName(LocalDate closingDate, String cardName) {
      log.info("Retrieving invoice for closing date '{}'", closingDate);

      var invoice = invoiceRepository.findInvoiceByClosingDateAndCardName(closingDate, cardName);

      if (invoice.isEmpty()) {
        log.info("No invoice found for the specified closing date '{}'", closingDate);

        return Optional.empty();
      }

      log.info("Invoice found: {}", invoice.get());
      return invoice;
    }

    public BigDecimal sumInvoiceExpenses(InvoiceEntity invoice) {
        log.info("Summing expenses for card '{}'", invoice.getCard().getName());

        if (invoice.getWasManuallyAdded()) {
          log.info("Invoice was manually altered");
          return invoice.getAmount();
        }

        var totalAmount = invoice.getExpenses().stream()
            .filter(expense -> !expense.getIsIgnored())
            .filter(expense -> expense.getPaymentType().equals(PaymentTypeEnum.CREDIT))
            .map(ExpenseEntity::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setAmount(totalAmount);
        invoiceRepository.save(invoice);

        log.info("Total amount for invoice ID {}: {}", invoice.getId(), totalAmount);
        return totalAmount;
    }
}
