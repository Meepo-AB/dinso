package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import se.meepo.dinso.service.CustomerId;

@Entity @Table(name = "person", uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "external_id"}))
public class PersonEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
  @Enumerated(EnumType.STRING) @Column(name = "customer_id", nullable = false) private CustomerId customerId;
  @Column(name = "external_id", nullable = false) private String externalId;
  @Column(nullable = false) private String displayName;
  protected PersonEntity() { }
  public PersonEntity(CustomerId customerId, String externalId, String displayName) { this.customerId = customerId; this.externalId = externalId; this.displayName = displayName; }
  public String getId() { return id; } public CustomerId getCustomerId() { return customerId; } public String getDisplayName() { return displayName; }
}
