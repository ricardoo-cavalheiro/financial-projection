package com.example.financial_app.domain.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.example.financial_app.domain.entities.IncomeEntity;

public interface IIncomeService {
  public void addRecurringIncome(
    String incomeName,
    BigDecimal amount,
    Integer recurrenceDay
  );

  public void addOneTimeIncome(
    String incomeName,
    BigDecimal amount,
    LocalDate paymentDate
  );

  public BigDecimal sumMonthlyIncome(YearMonth referenceDate);

  public List<IncomeEntity> getAllByPaymentDate(YearMonth referenceDate);

  public List<IncomeEntity> getAllIncomes();
}
