package com.example.financial_app.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
  private final ExpenseService expenseService;
  private final IncomeService incomeService;

  private final static int PROJECTION_MONTHS = 12;

  @Command(command = "expenses-projection", description = "Project expenses for the next 12 months.")
  public void expensesProjection() {
    var currentDate = LocalDate.now();
    var cards = cardService.getAllCards();

    log.info("Found {} cards for projection", cards.size());

    for (var i = 0; i < PROJECTION_MONTHS; i++) {
      var currentMonthIteration = currentDate.plusMonths(i);
      log.info("Date: {}", DateTimeFormatter.ofPattern("MM/yyyy").format(currentMonthIteration));

      var currentMonthIncomes = incomeService.getAllIncomes();

      for (var income : currentMonthIncomes) {
        log.info("Income: {}", income.getAmount());
      }

      var currentMonthDebitExpenses = expenseService.getAllDebitExpensesByDate(currentMonthIteration);
      var currentMonthDebtTotalAmount = expenseService.sumDebitExpenses(currentMonthDebitExpenses);

      log.info("Debit amount: {}", currentMonthDebtTotalAmount);

      for (var card : cards) {
        var currentMonthInvoice = invoiceService.getInvoiceByClosingDateAndCardName(
          currentDate.withDayOfMonth(card.getClosingDay()),
          card.getName()
        );
        
        var currentMonthInvoiceTotalAmount = invoiceService.sumInvoiceExpenses(currentMonthInvoice);
        log.info("Card: {} | Invoice amount: {}", card.getName(), currentMonthInvoiceTotalAmount);
      }
    }
  }
}
