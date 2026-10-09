package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import se.meepo.dinso.service.CustomerId;

@Entity
@Table(name = "insurance")
public class InsuranceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "customer_id", nullable = false)
  private CustomerId customerId;

  @ManyToOne(optional = false)
  private PersonEntity person;

  @Column(nullable = false)
  private String productName;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String status;

  @Column(name = "insurance_value", nullable = false, precision = 14, scale = 2)
  private BigDecimal value;

  protected InsuranceEntity() {}

  public InsuranceEntity(
      CustomerId customerId,
      PersonEntity person,
      String productName,
      String type,
      String status,
      BigDecimal value) {
    this.customerId = customerId;
    this.person = person;
    this.productName = productName;
    this.type = type;
    this.status = status;
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public PersonEntity getPerson() {
    return person;
  }

  public String getProductName() {
    return productName;
  }

  public String getType() {
    return type;
  }

  public String getStatus() {
    return status;
  }

  public BigDecimal getValue() {
    return value;
  }
}
