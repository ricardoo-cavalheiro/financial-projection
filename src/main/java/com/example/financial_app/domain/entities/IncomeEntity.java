package com.example.financial_app.domain.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

@Builder
@Entity(name = "income")
@Table(name = "income")
public class IncomeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "description")
  private String description;

  @Column(name = "amount")
  private Double amount;

  @Column(name = "recurrence_day")
  private Integer recurrenceDay;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;
}
