package com.example.financial_app.services;

import static java.util.Objects.nonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import com.example.financial_app.domain.entities.CardEntity;
import com.example.financial_app.domain.entities.ExpenseEntity;
import com.example.financial_app.domain.enums.PaymentTypeEnum;
import com.example.financial_app.repositories.ICardRepository;
import com.example.financial_app.repositories.IExpenseRepository;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Command(group = "Expense", description = "Commands related to expense management.")
@RequiredArgsConstructor
public class ExpenseService {
  private final ICardRepository cardRepository;
  private final IExpenseRepository expenseRepository;

  @Command(command = "add-recurring-debit-expense", description = "Add a new recurringdebit expense.")
  public void addDebitExpense(
      @Option(required = true) @Size(min = 3, max = 20, message = "Invalid expense name. Name must be between 3 and 20 characters.") String expenseName,
      @Option(required = true) @Min(0) Double amount,
      @Option(required = true) @FutureOrPresent LocalDate paymentDate
  ) {
    log.info("Adding debit expense with name: {}, amount: {}, payment date: {}", expenseName, amount, paymentDate);

    var currentDate = LocalDate.now();

    var expenses = new ArrayList<ExpenseEntity>();
    for (int i = 0; i < 12; i++) {
      var currentIterationMonth = currentDate.withDayOfMonth(paymentDate.getDayOfMonth()).plusMonths(i);

      var expense = ExpenseEntity.builder()
          .description(expenseName)
          .amount(amount)
          .paymentType(PaymentTypeEnum.DEBIT)
          .isRecurring(Boolean.TRUE)
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
      @Option(required = true) @Size(min = 3, max = 20, message = "Invalid expense name. Name must be between 3 and 20 characters.") String expenseName,
      @Option(required = true) @Min(0) Double amount,
      @Option(required = true) @FutureOrPresent LocalDate paymentDate
  ) {
    log.info("Adding one-time debit expense with name: {}, amount: {}, payment date: {}", expenseName, amount, paymentDate);

    var currentDate = LocalDate.now();

    var expense = ExpenseEntity.builder()
        .description(expenseName)
        .amount(amount)
        .paymentType(PaymentTypeEnum.DEBIT)
        .isRecurring(Boolean.FALSE)
        .isPaid(paymentDate.isBefore(currentDate) || paymentDate.isEqual(currentDate))
        .paymentDate(paymentDate)
        .build();

    expenseRepository.save(expense);

    log.info("One-time debit expense added successfully!");
  }

  @Command(command = "add-recurring-credit-expense", description = "Add a new recurring credit expense.")
  public void addExpense(
      @Option(required = true) @Size(min = 3, max = 20, message = "Invalid expense name. Name must be between 3 and 20 characters.") String expenseName,
      @Option(required = true) @Min(0) Double amount,
      @Option(required = true) @FutureOrPresent LocalDate paymentDate,
      @Option(required = false) @Size(min = 3, max = 20, message = "Invalid card name. Name must be between 3 and 20 characters.") String cardName,
      @Option(required = false) @Min(1) Integer totalInstallments,
      @Option(required = false) @Min(1) Integer installmentNumber) {
    log.info(
        "Adding expense for the next 12 months with name: {}, amount: {}, payment date: {}",
        expenseName, amount, paymentDate);

    CardEntity card = null;
    if (nonNull(cardName)) {
      card = cardRepository.findByName(cardName).orElseThrow(() -> {
        log.error("Card with name '{}' not found.", cardName);
        return new IllegalArgumentException("Card not found: " + cardName);
      });
    }

    var currentDate = LocalDate.now();
    var expenses = new ArrayList<ExpenseEntity>();
    for (int i = 0; i < totalInstallments; i++) {
      var currentIterationMonth = currentDate.withDayOfMonth(paymentDate.getDayOfMonth()).plusMonths(i);

      var installmentNumberForCurrentIteration = installmentNumber + i;

      if (installmentNumberForCurrentIteration > totalInstallments) {
        continue; // No need to add expense if it's the last installment
      }

      var expense = ExpenseEntity.builder()
          .description(expenseName)
          .amount(amount)
          .paymentType(PaymentTypeEnum.CREDIT)
          .isRecurring(Boolean.TRUE)
          .card(card)
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
  public void addOneTimeExpense(
      @Option(required = true) @Size(min = 3, max = 20, message = "Invalid expense name. Name must be between 3 and 20 characters.") String expenseName,
      @Option(required = true) @Min(0) Double amount,
      @Option(required = true) @FutureOrPresent LocalDate paymentDate,
      @Option(required = false) @Size(min = 3, max = 20, message = "Invalid card name. Name must be between 3 and 20 characters.") String cardName) {
    log.info("Adding one-time credit expense with name: {}, amount: {}, payment date: {}", expenseName, amount, paymentDate);

    CardEntity card = null;
    if (nonNull(cardName)) {
      card = cardRepository.findByName(cardName).orElseThrow(() -> {
        log.error("Card with name '{}' not found.", cardName);
        return new IllegalArgumentException("Card not found: " + cardName);
      });
    }

    var currentDate = LocalDate.now();

    var expense = ExpenseEntity.builder()
        .description(expenseName)
        .amount(amount)
        .paymentType(PaymentTypeEnum.CREDIT)
        .isRecurring(Boolean.FALSE)
        .card(card)
        .isPaid(paymentDate.isBefore(currentDate) || paymentDate.isEqual(currentDate))
        .paymentDate(paymentDate)
        .build();

    expenseRepository.save(expense);

    log.info("One-time credit expense added successfully!");
  }

  @Command(command = "list-expenses", description = "List all expenses for the next 12 months")
  public void listExpenses() {
    log.info("Listing all expenses...");

    var expenses = (List<ExpenseEntity>) expenseRepository.findAll();
    if (expenses.isEmpty()) {
      log.info("No expenses found.");
      return;
    }

    expenses.forEach(expense -> log.info(expense.toString()));
  }
}
