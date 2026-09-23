package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import se.meepo.dinso.service.CustomerId;

@Entity
@Table(name = "pension_plan")
public class PensionPlanEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "customer_id", nullable = false)
  private CustomerId customerId;

  @ManyToOne(optional = false)
  private CompanyEntity company;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal monthlyPremium;

  protected PensionPlanEntity() {}

  public PensionPlanEntity(
      CustomerId customerId, CompanyEntity company, String name, BigDecimal monthlyPremium) {
    this.customerId = customerId;
    this.company = company;
    this.name = name;
    this.monthlyPremium = monthlyPremium;
  }

  public String getId() {
    return id;
  }

  public CompanyEntity getCompany() {
    return company;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getMonthlyPremium() {
    return monthlyPremium;
  }
}
