package com.example.financial_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.transaction.annotation.Transactional;

import com.example.financial_app.domain.entities.ExpenseEntity;
import com.example.financial_app.domain.entities.IncomeEntity;
import com.example.financial_app.domain.enums.PaymentTypeEnum;
import com.example.financial_app.repositories.IIncomeRepository;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Command(group = "Income", description = "Commands related to income management")
@RequiredArgsConstructor
public class IncomeService {
  private final IIncomeRepository incomeRepository;

  @Command(command = "add-recurring-income", description = "Add a new recurring income")
  public void addRecurringIncome(
    @Option(required = true) @Size(min=3, max=20, message = "Invalid income name. Name must be between 3 and 20 characters.") String incomeName,
    @Option(required = true) @Min(0) BigDecimal amount,
    @Option(required = true) @Min(1) Integer recurrenceDay
  ) {
    log.info(
      "Adding recurring income with name: {}, amount: {}, recurrence day: {}", 
      incomeName, amount, recurrenceDay
    );

    var currentDate = LocalDate.now();
    var incomes = new ArrayList<IncomeEntity>();
    for (int i = 0; i < 12; i++) {
      var yearMonth = YearMonth.from(currentDate.plusMonths(i));
      var day = Math.min(recurrenceDay, yearMonth.lengthOfMonth());
      var currentIterationMonth = yearMonth.atDay(day);

      var income = IncomeEntity.builder()
        .description(incomeName)
        .amount(amount)
        .recurrenceDay(recurrenceDay)
        .isRecurring(Boolean.TRUE)
        .paymentDate(currentIterationMonth)
        .build();

      incomes.add(income);
    }

    incomeRepository.saveAll(incomes);

    log.info("Income added successfully!");
  }

  @Command(command = "add-one-time-income", description = "Add a new one-time income")
  public void addOneTimeIncome(
    @Option(required = true) 
    @Size(min=3, max=20, message = "Invalid income name. Name must be between 3 and 20 characters.") 
    String incomeName,
    @Option(required = true) @Min(0) BigDecimal amount,
    @Option(required = true) LocalDate paymentDate
  ) {
    log.info(
      "Adding one time income with name: {}, amount: {}, payment date: {}", 
      incomeName, amount, paymentDate
    );

    var income = IncomeEntity.builder()
      .description(incomeName)
      .amount(amount)
      .recurrenceDay(null)
      .paymentDate(paymentDate)
      .isRecurring(Boolean.FALSE)
      .build();

    incomeRepository.save(income);

    log.info("Income added successfully!");
  }

  public BigDecimal sumMonthlyIncome(YearMonth referenceDate) {
    log.info("Calculating total income for month: {}", referenceDate);
    
    var totalIncome = incomeRepository.findAllByPaymentDateBetween(
      referenceDate.atDay(1), 
      referenceDate.atEndOfMonth()
    ).stream()
      .map(IncomeEntity::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    log.info("Total income for {}: {}", referenceDate, totalIncome);
    return totalIncome;
  }

  @Command(command = "get-all-incomes", description = "Retrieve all incomes")
  public List<IncomeEntity> getAllIncomes() {
    log.info("Retrieving all incomes");
    return (List<IncomeEntity>) incomeRepository.findAll();
  }
}
