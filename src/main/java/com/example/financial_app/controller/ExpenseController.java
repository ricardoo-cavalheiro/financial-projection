package com.example.financial_app.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import com.example.financial_app.domain.services.IExpenseService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Command(group = "Expense", description = "Commands related to expense management.")
@RequiredArgsConstructor
public class ExpenseController {
  private final IExpenseService expenseService;

  @Command(command = "add-recurring-debit-expense", description = "Add a new recurring debit expense.")
  public void addRecurringDebitExpense(
    @Option(required = true) 
    @Size(min = 3, max = 20, message = "Invalid expense name. Name must be between 3 and 20 characters.") 
    String expenseName,
    @Option(required = true) @Min(0) BigDecimal amount,
    @Option(required = true) @Min(1) @Max(31) Integer paymentDay
  ) {
    expenseService.addRecurringDebitExpense(expenseName, amount, paymentDay);
  }

  @Command(command = "add-onetime-debit-expense", description = "Add a new one-time debit expense.")
  public void addOneTimeDebitExpense(
    @Option(required = true) 
    @Size(min = 3, max = 20, message = "Invalid expense name. Name must be between 3 and 20 characters.") 
    String expenseName,
    @Option(required = true) @Min(0) BigDecimal amount,
    @Option(required = true) LocalDate paymentDate
  ) {
    expenseService.addOneTimeDebitExpense(expenseName, amount, paymentDate);
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
    @Option(required = false) @Min(1) Integer installmentNumber
  ) {
    expenseService.addRecurringCreditExpense(
        expenseName, 
        amount, 
        paymentDay, 
        cardName, 
        totalInstallments, 
        installmentNumber
    );
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
    String cardName
  ) {
    expenseService.addOneTimeCreditExpense(expenseName, amount, paymentDate, cardName);
  }
}
