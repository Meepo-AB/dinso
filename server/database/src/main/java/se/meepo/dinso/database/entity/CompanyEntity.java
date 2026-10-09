package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import se.meepo.dinso.service.CustomerId;

@Entity
@Table(
    name = "company",
    uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "external_id"}))
public class CompanyEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "customer_id", nullable = false)
  private CustomerId customerId;

  @Column(name = "external_id", nullable = false)
  private String externalId;

  @Column(nullable = false)
  private String name;

  protected CompanyEntity() {}

  public CompanyEntity(CustomerId customerId, String externalId, String name) {
    this.customerId = customerId;
    this.externalId = externalId;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public CustomerId getCustomerId() {
    return customerId;
  }
}
