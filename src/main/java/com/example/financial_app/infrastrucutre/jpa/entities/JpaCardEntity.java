package com.example.financial_app.infrastrucutre.jpa.entities;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity(name = "CardEntity")
@Table(name = "card")
public class JpaCardEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "limit_amount")
  private BigDecimal limitAmount;

  @Column(name = "closing_day")
  private Integer closingDay;

  @Column(name = "payment_day")
  private Integer paymentDay;

  @OneToMany(mappedBy = "card")
  private List<JpaInvoiceEntity> invoices;
}
