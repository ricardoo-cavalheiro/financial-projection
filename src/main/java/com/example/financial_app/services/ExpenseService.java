package com.example.financial_app.services;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.financial_app.domain.dao.IExpenseRepository;
import com.example.financial_app.domain.entities.ExpenseEntity;
import com.example.financial_app.domain.services.IExpenseService;
import com.example.financial_app.domain.services.IInvoiceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ExpenseService implements IExpenseService {
  private final Clock clock;
  private final CardService cardService;
  private final IInvoiceService invoiceService;
  private final IExpenseRepository expenseRepository;

  @Override
  public void addRecurringDebitExpense(
      String expenseName,
      BigDecimal amount,
      Integer paymentDay
  ) {
    log.info("Adding debit expense with name: {}, amount: {}, payment day: {}", expenseName, amount, paymentDay);

    var expenses = new ArrayList<ExpenseEntity>();
    for (int monthOffset = 0; monthOffset < 12; monthOffset++) {
      var currentIterationPaymentDate = LocalDate.now(clock)
        .plusMonths(monthOffset)
        .withDayOfMonth(paymentDay);

      var expense = ExpenseEntity.createDebit(
        expenseName, 
        amount, 
        currentIterationPaymentDate, 
        Boolean.TRUE,
        clock
      );

      expenses.add(expense);
    }

    expenseRepository.saveAll(expenses);

    log.info("Recurring debit expense added successfully!");
  }

  @Override
  public void addOneTimeDebitExpense(
      String expenseName,
      BigDecimal amount,
      LocalDate paymentDate
  ) {
    log.info("Adding one-time debit expense with name: {}, amount: {}, payment date: {}", expenseName, amount, paymentDate);

    var expense = ExpenseEntity.createDebit(
      expenseName, 
      amount, 
      paymentDate,
      Boolean.FALSE,
      clock
    );

    expenseRepository.save(expense);

    log.info("One-time debit expense added successfully!");
  }

  @Override
  public void addRecurringCreditExpense(
    String expenseName,
    BigDecimal amount,
    Integer paymentDay, 
    String cardName,
    Integer totalInstallments,
    Integer installmentNumber
  ) {
    log.info(
        "Adding expense for the next 12 months with name: {}, amount: {}, payment day: {}",
        expenseName, amount, paymentDay);

    var card = cardService.getCard(cardName);

    var currentDate = LocalDate.now(clock);
    var expenses = new ArrayList<ExpenseEntity>();

    var monthsChecked = 0;
    var installmentsAdded = 0;
    while (installmentsAdded < totalInstallments) {
      var currentIterationDate = currentDate.withDayOfMonth(paymentDay).plusMonths(monthsChecked);
      var currentIterationInvoiceDate = currentIterationDate.withDayOfMonth(card.getClosingDay());
      var currentMonthInvoice = invoiceService.getInvoiceByClosingDateAndCardName(currentIterationInvoiceDate, cardName);

      if (currentMonthInvoice.getIsPaid()) {
        log.warn("Invoice for {} is already paid. Skipping expense creation for this month.", currentMonthInvoice.getClosingDate());
        monthsChecked++;
        continue; // Skip if the invoice is already paid
      }

      var installmentNumberForCurrentIteration = installmentNumber + installmentsAdded;

      if (installmentNumberForCurrentIteration > totalInstallments) {
        break; // No need to add expense if it's the last installment
      }

      var expense = ExpenseEntity.createCredit(
          expenseName, 
          amount, 
          currentIterationDate,
          Boolean.TRUE,
          card,
          currentMonthInvoice,
          clock);

      expenses.add(expense);
      installmentsAdded++;
      monthsChecked++;
    }

    expenseRepository.saveAll(expenses);

    log.info("Expense added successfully!");
  }

  @Override
  public void addOneTimeCreditExpense(
    String expenseName,
    BigDecimal amount,
    LocalDate paymentDate,
    String cardName
  ) {
    log.info("Adding one-time credit expense with name: {}, amount: {}, payment date: {}", expenseName, amount, paymentDate);

    var card = cardService.getCard(cardName);
    var currentMonthInvoiceDate = LocalDate.now(clock).withDayOfMonth(card.getClosingDay());

    var currentMonthInvoice = invoiceService.getInvoiceByClosingDateAndCardName(
      currentMonthInvoiceDate,
      cardName
    );

    if (currentMonthInvoice.getIsPaid()) {
      currentMonthInvoice = invoiceService.getInvoiceByClosingDateAndCardName(
        currentMonthInvoiceDate.plusMonths(1),
        cardName
      );
    }

    var expense = ExpenseEntity.createCredit(
      expenseName, 
      amount, 
      paymentDate,
      Boolean.FALSE,
      card,
      currentMonthInvoice,
      clock);

    expenseRepository.save(expense);

    log.info("One-time credit expense added successfully!");
  }

  @Override
  public List<ExpenseEntity> getDebitExpenses(Integer months) {
    log.info("Retrieving all debit expenses for the next {} months.", months);

    var currentDate = LocalDate.now(clock);
    var startDate = YearMonth.from(currentDate).atDay(1);
    var endDate = YearMonth.from(currentDate).atEndOfMonth();
    var debitExpenses = expenseRepository.findAllDebitExpenses(
      startDate,
      endDate
    );

    if (debitExpenses.isEmpty()) {
      log.info("No debit expenses found.");
    } else {
      debitExpenses.forEach(expense -> log.info(expense.toString()));
    }

    var expenses = new ArrayList<ExpenseEntity>();
    for (var expense : debitExpenses) {
      expenseRepository.findAllDebitExpensesByDescription(
          startDate,
          expense,
          Limit.of(months),
          Sort.by(Sort.Direction.ASC, "paymentDate"))
        .forEach(expenses::add);
    }

    return expenses;
  }

  @Override
  public List<ExpenseEntity> getAllDebitExpensesByDate(LocalDate currentMonthIteration) {
    log.info("Retrieving all debit expenses by date.");

    var startDate = YearMonth.from(currentMonthIteration).atDay(1);
    var endDate = YearMonth.from(currentMonthIteration).atEndOfMonth();
    
    var debitExpenses = expenseRepository.findAllDebitExpensesByYearMonth(
      startDate,
      endDate
    );

    if (debitExpenses.isEmpty()) {
      log.info("No debit expenses found.");
      return List.of();
    }

    debitExpenses.forEach(expense -> log.info(expense.toString()));
    
    return debitExpenses;
  }

  @Override
  public BigDecimal sumDebitExpenses(List<ExpenseEntity> debitExpenses) {
    log.info("Summing all debit expenses.");

    if (debitExpenses.isEmpty()) {
      log.info("No debit expenses found for.");
      return BigDecimal.ZERO;
    }

    var totalAmount = debitExpenses.stream()
        .map(ExpenseEntity::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return totalAmount;
  }
}
