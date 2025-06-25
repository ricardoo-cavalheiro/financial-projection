package com.example.financial_app.services;

import java.math.BigDecimal;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import com.example.financial_app.domain.entities.IncomeEntity;
import com.example.financial_app.repositories.IIncomeRepository;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Command(group = "Income", description = "Commands related to income management")
@RequiredArgsConstructor
public class IncomeService {
  private final IIncomeRepository incomeRepository;

  @Command(command = "add-income", description = "Add a new income")
  public void addIncome(
    @Option(required = true) @Size(min=3, max=20, message = "Invalid income name. Name must be between 3 and 20 characters.") String incomeName,
    @Option(required = true) @Min(0) BigDecimal amount,
    @Option(required = true) @Min(1) Integer recurrenceDay
  ) {
    log.info(
      "Adding income with name: {}, amount: {}, recurrence day: {}", 
      incomeName, amount, recurrenceDay
    );

    var income = IncomeEntity.builder()
      .description(incomeName)
      .amount(amount)
      .recurrenceDay(recurrenceDay)
      .build();

    incomeRepository.save(income);

    log.info("Income added successfully!");
  }
}
