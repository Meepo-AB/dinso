package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import se.meepo.dinso.service.CustomerId;

@Entity
@Table(name = "company_case")
public class CompanyCaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "customer_id", nullable = false)
  private CustomerId customerId;

  @ManyToOne(optional = false)
  private CompanyEntity company;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private LocalDate dueOn;

  @Column(nullable = false, length = 1000)
  private String detail;

  protected CompanyCaseEntity() {}

  public CompanyCaseEntity(
      CustomerId customerId,
      CompanyEntity company,
      String title,
      String status,
      LocalDate dueOn,
      String detail) {
    this.customerId = customerId;
    this.company = company;
    this.title = title;
    this.status = status;
    this.dueOn = dueOn;
    this.detail = detail;
  }

  public String getId() {
    return id;
  }

  public CompanyEntity getCompany() {
    return company;
  }

  public String getTitle() {
    return title;
  }

  public String getStatus() {
    return status;
  }

  public LocalDate getDueOn() {
    return dueOn;
  }

  public String getDetail() {
    return detail;
  }

  public void approve() {
    status = "APPROVED";
  }
}
