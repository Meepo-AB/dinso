package se.meepo.dinso.database.entity;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "demo_session")
public class DemoSessionEntity {
  @Id private String token;
  @ManyToOne(optional = false) private DemoProfileEntity profile;
  @Column(nullable = false) private Instant expiresAt;
  @Column(nullable = false) private boolean active = true;
  protected DemoSessionEntity() { }
  public DemoSessionEntity(String token, DemoProfileEntity profile, Instant expiresAt) { this.token = token; this.profile = profile; this.expiresAt = expiresAt; }
  public String getToken() { return token; } public DemoProfileEntity getProfile() { return profile; } public Instant getExpiresAt() { return expiresAt; } public boolean isActive() { return active; } public void invalidate() { active = false; }
}
