package se.meepo.dinso.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity @Table(name = "transaction")
public class TransactionEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
  @ManyToOne(optional = false) private InsuranceEntity insurance;
  @Column(nullable = false) private LocalDate bookedOn;
  @Column(nullable = false) private String description;
  @Column(nullable = false, precision = 14, scale = 2) private BigDecimal amount;
  @Column(nullable = false) private String status;
  protected TransactionEntity() { }
  public TransactionEntity(InsuranceEntity insurance, LocalDate bookedOn, String description, BigDecimal amount, String status) { this.insurance = insurance; this.bookedOn = bookedOn; this.description = description; this.amount = amount; this.status = status; }
  public InsuranceEntity getInsurance() { return insurance; } public LocalDate getBookedOn() { return bookedOn; } public String getDescription() { return description; } public BigDecimal getAmount() { return amount; } public String getStatus() { return status; }
}
