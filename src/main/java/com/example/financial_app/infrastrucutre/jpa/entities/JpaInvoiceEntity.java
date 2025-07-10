package com.example.financial_app.infrastrucutre.jpa.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity(name = "InvoiceEntity")
@Table(name = "invoice")
public class JpaInvoiceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "is_paid")
  private Boolean isPaid;

  @Column(name = "closing_date")
  private LocalDate closingDate;

  @Column(name = "payment_date")
  private LocalDate paymentDate;

  @Column(name = "was_manually_added")
  private Boolean wasManuallyAdded;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "card_id")
  private JpaCardEntity card;

  @OneToMany(mappedBy = "invoice")
  private List<JpaExpenseEntity> expenses;  
}
