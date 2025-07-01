package com.example.financial_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

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
      var totalOutAmount = BigDecimal.ZERO;
      var totalInAmount = BigDecimal.ZERO;
      var currentMonthIteration = currentDate.plusMonths(i).withDayOfMonth(1);

      var incomes = incomeService.getAllByPaymentDate(YearMonth.of(currentMonthIteration.getYear(), currentMonthIteration.getMonth()));
      for (var income : incomes) {
        log.info("Date: {} | Income: {} | Amount: {}", income.getPaymentDate(), income.getDescription(), income.getAmount());
      }

      totalInAmount = incomeService.sumMonthlyIncome(YearMonth.from(currentMonthIteration));

      var currentMonthDebitExpenses = expenseService.getAllDebitExpensesByDate(currentMonthIteration);
      var currentMonthDebtTotalAmount = expenseService.sumDebitExpenses(currentMonthDebitExpenses);
      totalOutAmount = totalOutAmount.add(currentMonthDebtTotalAmount);

      log.info("Date: {} | Debit amount: {}", currentMonthIteration, currentMonthDebtTotalAmount);

      for (var card : cards) {
        var currentMonthInvoice = invoiceService.getInvoiceByClosingDateAndCardName(
          currentMonthIteration.withDayOfMonth(card.getClosingDay()),
          card.getName()
        );

        if (currentMonthInvoice.equals(Optional.empty())) {
          log.warn("No invoice found for card: {} on date: {}", card.getName(), currentMonthIteration);
          continue;
        }
        
        var currentMonthInvoiceTotalAmount = invoiceService.sumInvoiceExpenses(currentMonthInvoice.get());
        totalOutAmount = totalOutAmount.add(currentMonthInvoiceTotalAmount);
        log.info(
          "Date: {} | Card: {} | Invoice amount: {}", 
          currentMonthIteration.withDayOfMonth(card.getPaymentDay()), 
          card.getName(), 
          currentMonthInvoiceTotalAmount
        );
      }

      log.info("TOTAL IN: {}", totalInAmount);
      log.info("TOTAL OUT: {}", totalOutAmount);
    }
  }
}
