package com.example.financial_app.services;

import java.math.BigDecimal;
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
import com.example.financial_app.domain.enums.PaymentTypeEnum;
import com.example.financial_app.domain.services.IExpenseService;
import com.example.financial_app.domain.services.IInvoiceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ExpenseService implements IExpenseService {
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

    var currentDate = LocalDate.now();

    var expenses = new ArrayList<ExpenseEntity>();
    for (int i = 0; i < 12; i++) {
      var currentIterationMonth = currentDate.withDayOfMonth(paymentDay).plusMonths(i);

      var expense = ExpenseEntity.builder()
          .description(expenseName)
          .amount(amount)
          .paymentType(PaymentTypeEnum.DEBIT)
          .isRecurring(Boolean.TRUE)
          .isIgnored(Boolean.FALSE)
          .isPaid(currentIterationMonth.isBefore(currentDate) || currentIterationMonth.isEqual(currentDate))
          .paymentDate(currentIterationMonth)
          .build();

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

    var currentDate = LocalDate.now();

    var expense = ExpenseEntity.builder()
        .description(expenseName)
        .amount(amount)
        .paymentType(PaymentTypeEnum.DEBIT)
        .isRecurring(Boolean.FALSE)
        .isIgnored(Boolean.FALSE)
        .isPaid(paymentDate.isBefore(currentDate) || paymentDate.isEqual(currentDate))
        .paymentDate(paymentDate)
        .build();

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
      Integer installmentNumber) {
    log.info(
        "Adding expense for the next 12 months with name: {}, amount: {}, payment day: {}",
        expenseName, amount, paymentDay);

    var card = cardService.getCard(cardName);

    var currentDate = LocalDate.now();
    var expenses = new ArrayList<ExpenseEntity>();

    var monthsChecked = 0;
    var installmentsAdded = 0;
    while (installmentsAdded < totalInstallments) {
      var currentIterationMonth = currentDate.withDayOfMonth(paymentDay).plusMonths(monthsChecked);
      var currentIterationInvoiceDate = currentIterationMonth.withDayOfMonth(card.getClosingDay());
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

      var expense = ExpenseEntity.builder()
          .description(expenseName)
          .amount(amount)
          .paymentType(PaymentTypeEnum.CREDIT)
          .isRecurring(Boolean.TRUE)
          .isIgnored(Boolean.FALSE)
          .card(card)
          .invoice(currentMonthInvoice)
          .isPaid(currentIterationMonth.isBefore(currentDate) || currentIterationMonth.isEqual(currentDate))
          .paymentDate(currentIterationMonth)
          .totalInstallments(totalInstallments)
          .installmentNumber(installmentNumberForCurrentIteration)
          .build();

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
      String cardName) {
    log.info("Adding one-time credit expense with name: {}, amount: {}, payment date: {}", expenseName, amount, paymentDate);

    var currentDate = LocalDate.now();
    var card = cardService.getCard(cardName);
    var currentMonthInvoice = invoiceService.getInvoiceByClosingDateAndCardName(
      currentDate.withDayOfMonth(card.getClosingDay()),
      cardName
    );

    if (currentMonthInvoice.getIsPaid()) {
      currentMonthInvoice = invoiceService.getInvoiceByClosingDateAndCardName(
        currentDate.plusMonths(1).withDayOfMonth(card.getClosingDay()),
        cardName
      );
    }

    var expense = ExpenseEntity.builder()
        .description(expenseName)
        .amount(amount)
        .paymentType(PaymentTypeEnum.CREDIT)
        .isRecurring(Boolean.FALSE)
        .isIgnored(Boolean.FALSE)
        .card(card)
        .invoice(currentMonthInvoice)
        .isPaid(paymentDate.isBefore(currentDate) || paymentDate.isEqual(currentDate))
        .paymentDate(paymentDate)
        .build();

    expenseRepository.save(expense);

    log.info("One-time credit expense added successfully!");
  }

  @Override
  public List<ExpenseEntity> getDebitExpenses(Integer months) {
    log.info("Retrieving all debit expenses for the next {} months.", months);

    var currentDate = LocalDate.now();
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
