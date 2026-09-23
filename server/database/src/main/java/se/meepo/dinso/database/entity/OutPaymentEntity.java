package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "out_payment")
public class OutPaymentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(optional = false)
  private PersonEntity person;

  @Column(nullable = false)
  private LocalDate paymentDate;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal grossAmount;

  @Column(nullable = false)
  private String status;

  protected OutPaymentEntity() {}

  public OutPaymentEntity(
      PersonEntity person, LocalDate paymentDate, BigDecimal grossAmount, String status) {
    this.person = person;
    this.paymentDate = paymentDate;
    this.grossAmount = grossAmount;
    this.status = status;
  }

  public LocalDate getPaymentDate() {
    return paymentDate;
  }

  public BigDecimal getGrossAmount() {
    return grossAmount;
  }

  public String getStatus() {
    return status;
  }
}
