package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice")
public class InvoiceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(optional = false)
  private CompanyEntity company;

  @Column(nullable = false)
  private LocalDate dueOn;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false)
  private String status;

  protected InvoiceEntity() {}

  public InvoiceEntity(CompanyEntity company, LocalDate dueOn, BigDecimal amount, String status) {
    this.company = company;
    this.dueOn = dueOn;
    this.amount = amount;
    this.status = status;
  }
}
