package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import se.meepo.dinso.service.DemoRole;

@Entity @Table(name = "company_authorization")
public class CompanyAuthorizationEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
  @ManyToOne(optional = false) private DemoProfileEntity profile;
  @ManyToOne(optional = false) private CompanyEntity company;
  @Enumerated(EnumType.STRING) @Column(nullable = false) private DemoRole role;
  protected CompanyAuthorizationEntity() { }
  public CompanyAuthorizationEntity(DemoProfileEntity profile, CompanyEntity company, DemoRole role) { this.profile = profile; this.company = company; this.role = role; }
  public CompanyEntity getCompany() { return company; } public DemoRole getRole() { return role; }
}
