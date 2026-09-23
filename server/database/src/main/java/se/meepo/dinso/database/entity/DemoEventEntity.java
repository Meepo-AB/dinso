package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import java.time.Instant;
import se.meepo.dinso.service.CustomerId;

@Entity
@Table(name = "demo_event")
public class DemoEventEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "customer_id", nullable = false)
  private CustomerId customerId;

  @Column(nullable = false)
  private Instant occurredAt;

  @Column(nullable = false)
  private String eventType;

  @Column(nullable = false)
  private String summary;

  protected DemoEventEntity() {}

  public DemoEventEntity(
      CustomerId customerId, Instant occurredAt, String eventType, String summary) {
    this.customerId = customerId;
    this.occurredAt = occurredAt;
    this.eventType = eventType;
    this.summary = summary;
  }
}
