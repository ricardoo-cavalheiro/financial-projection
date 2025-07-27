package com.example.financial_app.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import com.example.financial_app.domain.entities.IncomeEntity;
import com.example.financial_app.domain.services.IIncomeService;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Command(group = "Income", description = "Commands related to income management.")
@RequiredArgsConstructor
public class IncomeController {
  private final IIncomeService incomeService;

  @Command(command = "add-recurring-income", description = "Add a new recurring income")
  public void addRecurringIncome(
    @Option(required = true) @Size(min=3, max=20, message = "Invalid income name. Name must be between 3 and 20 characters.") String incomeName,
    @Option(required = true) @Min(0) BigDecimal amount,
    @Option(required = true) @Min(1) Integer recurrenceDay
  ) {
    incomeService.addRecurringIncome(incomeName, amount, recurrenceDay);
  }

  @Command(command = "add-one-time-income", description = "Add a new one-time income")
  public void addOneTimeIncome(
    @Option(required = true) 
    @Size(min=3, max=20, message = "Invalid income name. Name must be between 3 and 20 characters.") 
    String incomeName,
    @Option(required = true) @Min(0) BigDecimal amount,
    @Option(required = true) LocalDate paymentDate
  ) {
    incomeService.addOneTimeIncome(incomeName, amount, paymentDate);
  }

  @Command(command = "get-all-incomes", description = "Retrieve all incomes")
  public List<IncomeEntity> getAllIncomes() {
    return incomeService.getAllIncomes();
  }
}
