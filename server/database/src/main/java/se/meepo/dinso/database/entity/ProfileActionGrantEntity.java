package se.meepo.dinso.database.entity;

import jakarta.persistence.*;
import se.meepo.dinso.service.CompanyAction;

/**
 * A single granted {@link CompanyAction} for a demo profile. One row per
 * (profile, action) pair, mirroring the shape of
 * {@link CompanyAuthorizationEntity}. Scales fine to ~100 actions x ~100
 * profiles (~10k rows) for a demo H2 in-memory database.
 */
@Entity
@Table(name = "profile_action_grant", uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "action"}))
public class ProfileActionGrantEntity {
  @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
  @ManyToOne(optional = false) private DemoProfileEntity profile;
  @Enumerated(EnumType.STRING) @Column(nullable = false) private CompanyAction action;

  protected ProfileActionGrantEntity() { }

  public ProfileActionGrantEntity(DemoProfileEntity profile, CompanyAction action) {
    this.profile = profile;
    this.action = action;
  }

  public DemoProfileEntity getProfile() { return profile; }
  public CompanyAction getAction() { return action; }
}
