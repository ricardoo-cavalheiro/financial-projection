package com.example.financial_app.services;

import static java.util.Objects.nonNull;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import com.example.financial_app.domain.entities.CardEntity;
import com.example.financial_app.domain.entities.ExpenseEntity;
import com.example.financial_app.domain.enums.PaymentTypeEnum;
import com.example.financial_app.repositories.ICardRepository;
import com.example.financial_app.repositories.IExpenseRepository;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Command(group = "Expense", description = "Commands related to expense management")
@RequiredArgsConstructor
public class ExpenseService {
  private final ICardRepository cardRepository;
  private final IExpenseRepository expenseRepository;

  @Command(command = "add-expense", description = "Add a new expense")
  public void addExpense(
    @Option(required = true) @Size(min=3, max=20, message = "Invalid expense name. Name must be between 3 and 20 characters.") String expenseName,
    @Option(required = true) @Min(0) Double amount,
    @Option(required = true) PaymentTypeEnum paymentType,
    @Option(required = false, defaultValue = "false") Boolean isRecurring,
    @Option(required = false) @Min(1) Integer recurrenceDay,
    @Option(required = false) @Size(min=3, max=20, message = "Invalid card name. Name must be between 3 and 20 characters.") String cardName,
    @Option(required = false) @Min(1) Integer installments,
    @Option(required = false) @Min(1) Integer currentInstallment
  ) {
    log.info(
      "Adding expense with name: {}, amount: {}, recurrence day: {}", 
      expenseName, amount, recurrenceDay
    );

    CardEntity card = null;
    if (nonNull(cardName)) {
      card = cardRepository.findByName(cardName).orElseThrow(() -> {
        log.error("Card with name '{}' not found.", cardName);
        return new IllegalArgumentException("Card not found: " + cardName);
      });
    }

    var expense = ExpenseEntity.builder()
      .description(expenseName)
      .amount(amount)
      .paymentType(paymentType)
      .isRecurring(isRecurring)
      .recurrenceDay(recurrenceDay)
      .card(card)
      .installments(installments)
      .currentInstallment(currentInstallment)
      .build();

    expenseRepository.save(expense);

    log.info("Expense added successfully!");
  }
}
