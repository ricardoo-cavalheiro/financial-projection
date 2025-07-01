package com.example.financial_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.transaction.annotation.Transactional;

import com.example.financial_app.domain.entities.ExpenseEntity;
import com.example.financial_app.domain.enums.PaymentTypeEnum;
import com.example.financial_app.repositories.IExpenseRepository;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Command(group = "Expense", description = "Commands related to expense management.")
@RequiredArgsConstructor
public class ExpenseService {
  private final CardService cardService;
  private final InvoiceService invoiceService;
  private final IExpenseRepository expenseRepository;

  @Command(command = "add-recurring-debit-expense", description = "Add a new recurring debit expense.")
  public void addRecurringDebitExpense(
      @Option(required = true) 
      @Size(min = 3, max = 20, message = "Invalid expense name. Name must be between 3 and 20 characters.") 
      String expenseName,
      @Option(required = true) @Min(0) BigDecimal amount,
      @Option(required = true) @Min(1) @Max(31) Integer paymentDay
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

  @Command(command = "add-onetime-debit-expense", description = "Add a new one-time debit expense.")
  public void addOneTimeDebitExpense(
      @Option(required = true) 
      @Size(min = 3, max = 20, message = "Invalid expense name. Name must be between 3 and 20 characters.") 
      String expenseName,
      @Option(required = true) @Min(0) BigDecimal amount,
      @Option(required = true) LocalDate paymentDate
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

  @Command(command = "add-recurring-credit-expense", description = "Add a new recurring credit expense.")
  public void addRecurringCreditExpense(
      @Option(required = true) 
      @Size(min = 3, max = 20, message = "Invalid expense name. Name must be between 3 and 20 characters.") 
      String expenseName,
      @Option(required = true) @Min(0) BigDecimal amount,
      @Option(required = true) @Min(1) @Max(31) Integer paymentDay,
      @Option(required = false) 
      @Size(min = 3, max = 20, message = "Invalid card name. Name must be between 3 and 20 characters.") 
      String cardName,
      @Option(required = false) @Min(1) Integer totalInstallments,
      @Option(required = false) @Min(1) Integer installmentNumber) {
    log.info(
        "Adding expense for the next 12 months with name: {}, amount: {}, payment day: {}",
        expenseName, amount, paymentDay);

    var card = cardService.getCard(cardName);

    var currentDate = LocalDate.now();
    var expenses = new ArrayList<ExpenseEntity>();
    for (int i = 0; i < totalInstallments; i++) {
      var currentIterationMonth = currentDate.withDayOfMonth(paymentDay).plusMonths(i);
      var currentIterationInvoiceDate = currentIterationMonth.withDayOfMonth(card.getClosingDay());
      var currentMonthInvoice = invoiceService.getInvoiceByClosingDateAndCardName(currentIterationInvoiceDate, cardName);

      if (currentMonthInvoice.isEmpty()) {
        log.warn("No invoice found for card: {} on date: {}", cardName, currentIterationInvoiceDate);
        continue; // Skip this iteration if no invoice is found
      }

      var installmentNumberForCurrentIteration = installmentNumber + i;

      if (installmentNumberForCurrentIteration > totalInstallments) {
        continue; // No need to add expense if it's the last installment
      }

      var expense = ExpenseEntity.builder()
          .description(expenseName)
          .amount(amount)
          .paymentType(PaymentTypeEnum.CREDIT)
          .isRecurring(Boolean.TRUE)
          .isIgnored(Boolean.FALSE)
          .card(card)
          .invoice(currentMonthInvoice.get())
          .isPaid(currentIterationMonth.isBefore(currentDate) || currentIterationMonth.isEqual(currentDate))
          .paymentDate(currentIterationMonth)
          .totalInstallments(totalInstallments)
          .installmentNumber(installmentNumberForCurrentIteration)
          .build();

      expenses.add(expense);
    }

    expenseRepository.saveAll(expenses);

    log.info("Expense added successfully!");
  }

  @Command(command = "add-onetime-credit-expense", description = "Add a new one-time credit expense.")
  public void addOneTimeCreditExpense(
      @Option(required = true) 
      @Size(min = 3, max = 20, message = "Invalid expense name. Name must be between 3 and 20 characters.") 
      String expenseName,
      @Option(required = true) @Min(0) BigDecimal amount,
      @Option(required = true) LocalDate paymentDate,
      @Option(required = false) 
      @Size(min = 3, max = 20, message = "Invalid card name. Name must be between 3 and 20 characters.") 
      String cardName) {
    log.info("Adding one-time credit expense with name: {}, amount: {}, payment date: {}", expenseName, amount, paymentDate);

    var currentDate = LocalDate.now();
    var card = cardService.getCard(cardName);
    var currentMonthInvoice = invoiceService.getInvoiceByClosingDateAndCardName(
      currentDate.withDayOfMonth(card.getClosingDay()),
      cardName
    );

    if (currentMonthInvoice.isEmpty()) {
      log.warn("No invoice found for card: {} on date: {}", cardName, currentDate);
      return; // Exit if no invoice is found
    }

    var expense = ExpenseEntity.builder()
        .description(expenseName)
        .amount(amount)
        .paymentType(PaymentTypeEnum.CREDIT)
        .isRecurring(Boolean.FALSE)
        .isIgnored(Boolean.FALSE)
        .card(card)
        .invoice(currentMonthInvoice.get())
        .isPaid(paymentDate.isBefore(currentDate) || paymentDate.isEqual(currentDate))
        .paymentDate(paymentDate)
        .build();

    expenseRepository.save(expense);

    log.info("One-time credit expense added successfully!");
  }

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
