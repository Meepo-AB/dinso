package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fund_holding")
public class FundHoldingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(optional = false)
  private InsuranceEntity insurance;

  @Column(nullable = false)
  private String fundName;

  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal allocationPercent;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal marketValue;

  protected FundHoldingEntity() {}

  public FundHoldingEntity(
      InsuranceEntity insurance,
      String fundName,
      BigDecimal allocationPercent,
      BigDecimal marketValue) {
    this.insurance = insurance;
    this.fundName = fundName;
    this.allocationPercent = allocationPercent;
    this.marketValue = marketValue;
  }

  public String getFundName() {
    return fundName;
  }

  public BigDecimal getAllocationPercent() {
    return allocationPercent;
  }

  public BigDecimal getMarketValue() {
    return marketValue;
  }
}
