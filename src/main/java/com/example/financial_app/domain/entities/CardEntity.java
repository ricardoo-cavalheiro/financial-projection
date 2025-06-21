package com.example.financial_app.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

@Builder
@Entity(name = "card")
@Table(name = "card")
public class CardEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "limit_amount")
  private Double limitAmount;

  @Column(name = "closing_day")
  private Integer closingDay;

  @Column(name = "payment_day")
  private Integer paymentDay;
}
