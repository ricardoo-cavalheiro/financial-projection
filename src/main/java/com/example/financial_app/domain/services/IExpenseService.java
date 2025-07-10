package com.example.financial_app.domain.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.financial_app.domain.entities.ExpenseEntity;

public interface IExpenseService {
  public void addRecurringDebitExpense(
    String expenseName,
    BigDecimal amount,
    Integer paymentDay
  );

  public void addOneTimeDebitExpense(
    String expenseName,
    BigDecimal amount,
    LocalDate paymentDate
  );

  public void addRecurringCreditExpense(
    String expenseName,
    BigDecimal amount,
    Integer paymentDay,
    String cardName,
    Integer totalInstallments,
    Integer installmentNumber
  );

  public void addOneTimeCreditExpense(
    String expenseName,
    BigDecimal amount,
    LocalDate paymentDate,
    String cardName
  );

  public List<ExpenseEntity> getDebitExpenses(Integer months);

  public List<ExpenseEntity> getAllDebitExpensesByDate(LocalDate currentMonthIteration);

  public BigDecimal sumDebitExpenses(List<ExpenseEntity> debitExpenses);
}
