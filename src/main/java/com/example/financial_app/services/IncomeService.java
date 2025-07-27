package com.example.financial_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.financial_app.domain.dao.IIncomeRepository;
import com.example.financial_app.domain.entities.IncomeEntity;
import com.example.financial_app.domain.services.IIncomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class IncomeService implements IIncomeService {
  private final IIncomeRepository incomeRepository;

  public void addRecurringIncome(String incomeName, BigDecimal amount, Integer recurrenceDay) {
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

  public void addOneTimeIncome(String incomeName, BigDecimal amount, LocalDate paymentDate) {
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
    
    var incomes = getAllByPaymentDate(referenceDate);
    
    var totalIncome = incomes.stream()
      .map(IncomeEntity::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    log.info("Total income for {}: {}", referenceDate, totalIncome);
    return totalIncome;
  }

  public List<IncomeEntity> getAllByPaymentDate(YearMonth referenceDate) {
    log.info("Retrieving income for month: {}", referenceDate);
    return incomeRepository.findAllByPaymentDateBetween(referenceDate.atDay(1), referenceDate.atEndOfMonth());
  }

  public List<IncomeEntity> getAllIncomes() {
    log.info("Retrieving all incomes");
    return incomeRepository.findAll();
  }
}
