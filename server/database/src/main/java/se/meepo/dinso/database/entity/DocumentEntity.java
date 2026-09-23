package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import se.meepo.dinso.service.CustomerId;

@Entity
@Table(name = "document")
public class DocumentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "customer_id", nullable = false)
  private CustomerId customerId;

  @ManyToOne private PersonEntity person;
  @ManyToOne private CompanyEntity company;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private LocalDate publishedOn;

  protected DocumentEntity() {}

  public DocumentEntity(
      CustomerId customerId,
      PersonEntity person,
      CompanyEntity company,
      String title,
      String type,
      LocalDate publishedOn) {
    this.customerId = customerId;
    this.person = person;
    this.company = company;
    this.title = title;
    this.type = type;
    this.publishedOn = publishedOn;
  }

  public String getTitle() {
    return title;
  }

  public String getType() {
    return type;
  }

  public LocalDate getPublishedOn() {
    return publishedOn;
  }
}
