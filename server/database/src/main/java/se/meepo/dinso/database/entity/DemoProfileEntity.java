package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import se.meepo.dinso.service.*;

@Entity
@Table(
    name = "demo_profile",
    uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "external_id"}))
public class DemoProfileEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "external_id", nullable = false)
  private String externalId;

  @Enumerated(EnumType.STRING)
  @Column(name = "customer_id", nullable = false)
  private CustomerId customerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PortalType portal;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DemoRole role;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @ManyToOne private PersonEntity person;

  protected DemoProfileEntity() {}

  public DemoProfileEntity(DemoProfile value) {
    externalId = value.id();
    customerId = value.customerId();
    portal = value.portal();
    role = value.role();
    name = value.name();
    description = value.description();
  }

  public String getExternalId() {
    return externalId;
  }

  public PersonEntity getPerson() {
    return person;
  }

  public void assignPerson(PersonEntity person) {
    this.person = person;
  }

  public CustomerId getCustomerId() {
    return customerId;
  }

  public PortalType getPortal() {
    return portal;
  }

  public DemoRole getRole() {
    return role;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public DemoProfile toDomain() {
    return new DemoProfile(externalId, customerId, portal, role, name, description);
  }
}
