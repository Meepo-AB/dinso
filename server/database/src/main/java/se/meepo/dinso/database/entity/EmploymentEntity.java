package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import se.meepo.dinso.service.CustomerId;

@Entity
@Table(name = "employment")
public class EmploymentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "customer_id", nullable = false)
  private CustomerId customerId;

  @ManyToOne(optional = false)
  private PersonEntity person;

  @ManyToOne(optional = false)
  private CompanyEntity company;

  @ManyToOne(optional = false)
  private PensionPlanEntity pensionPlan;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal monthlySalary;

  @Column(nullable = false)
  private LocalDate startsOn;

  private LocalDate endsOn;

  @Column(nullable = false)
  private String status;

  protected EmploymentEntity() {}

  public EmploymentEntity(
      CustomerId customerId,
      PersonEntity person,
      CompanyEntity company,
      PensionPlanEntity pensionPlan,
      BigDecimal monthlySalary,
      LocalDate startsOn,
      String status) {
    this.customerId = customerId;
    this.person = person;
    this.company = company;
    this.pensionPlan = pensionPlan;
    this.monthlySalary = monthlySalary;
    this.startsOn = startsOn;
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public PersonEntity getPerson() {
    return person;
  }

  public CompanyEntity getCompany() {
    return company;
  }

  public PensionPlanEntity getPensionPlan() {
    return pensionPlan;
  }

  public BigDecimal getMonthlySalary() {
    return monthlySalary;
  }

  public LocalDate getStartsOn() {
    return startsOn;
  }

  public LocalDate getEndsOn() {
    return endsOn;
  }

  public String getStatus() {
    return status;
  }

  public void changeSalary(BigDecimal salary) {
    monthlySalary = salary;
  }

  public void registerLeave(LocalDate until) {
    status = "LEAVE";
    endsOn = until;
  }

  public void end(LocalDate date) {
    status = "ENDED";
    endsOn = date;
  }
}
