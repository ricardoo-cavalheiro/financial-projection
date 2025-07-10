package com.example.financial_app.infrastrucutre.jpa.adapters;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.example.financial_app.domain.dao.IExpenseRepository;
import com.example.financial_app.domain.entities.ExpenseEntity;
import com.example.financial_app.infrastrucutre.jpa.entities.JpaExpenseEntity;
import com.example.financial_app.infrastrucutre.jpa.ports.JpaExpenseRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaExpenseRepositoryAdapter implements IExpenseRepository {
    private final ModelMapper modelMapper;
    private final JpaExpenseRepositoryPort jpaExpenseRepositoryPort;

    @Override
    public ExpenseEntity save(ExpenseEntity expense) {
        var expenseJpaEntity = modelMapper.map(expense, JpaExpenseEntity.class);
        var savedExpenseJpaEntity = jpaExpenseRepositoryPort.save(expenseJpaEntity);

        return modelMapper.map(savedExpenseJpaEntity, ExpenseEntity.class);
    }

    @Override
    public List<ExpenseEntity> findAll() {
        var expenseJpaEntities = jpaExpenseRepositoryPort.findAll();

        return expenseJpaEntities
            .stream()
            .map(entity -> modelMapper.map(entity, ExpenseEntity.class))
            .toList();
    }

    @Override
    public Optional<ExpenseEntity> findById(Long id) {
        var expenseJpaEntity = jpaExpenseRepositoryPort.findById(id);

        if (expenseJpaEntity.isPresent()) {
            return Optional.of(modelMapper.map(expenseJpaEntity.get(), ExpenseEntity.class));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<ExpenseEntity> findAllDebitExpensesByDescription(
        LocalDate startDate, 
        String description, 
        Limit limit,
        Sort sort
    ) {
        var expenseJpaEntities = jpaExpenseRepositoryPort.findAllDebitExpensesByDescription(
                startDate, description, limit, sort);

        return expenseJpaEntities
            .stream()
            .map(entity -> modelMapper.map(entity, ExpenseEntity.class))
            .toList();
    }

    @Override
    public List<String> findAllDebitExpenses(LocalDate startDate, LocalDate endDate) {
        var descriptions = jpaExpenseRepositoryPort.findAllDebitExpenses(startDate, endDate);

        return descriptions
            .stream()
            .map(String::valueOf)
            .toList();
    }

    @Override
    public List<ExpenseEntity> findAllDebitExpensesByYearMonth(LocalDate startDate, LocalDate endDate) {
        var expenseJpaEntities = jpaExpenseRepositoryPort.findAllDebitExpensesByYearMonth(startDate, endDate);

        return expenseJpaEntities
            .stream()
            .map(entity -> modelMapper.map(entity, ExpenseEntity.class))
            .toList();
    }

    @Override
    public List<ExpenseEntity> saveAll(List<ExpenseEntity> expenses) {
        var expenseJpaEntities = expenses
            .stream()
            .map(expense -> modelMapper.map(expense, JpaExpenseEntity.class))
            .toList();

        var savedExpenseJpaEntities = jpaExpenseRepositoryPort.saveAll(expenseJpaEntities);

        return savedExpenseJpaEntities
            .stream()
            .map(entity -> modelMapper.map(entity, ExpenseEntity.class))
            .toList();
    }
}
