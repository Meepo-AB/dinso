package se.meepo.dinso.database;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.meepo.dinso.database.entity.DemoProfileEntity;
import se.meepo.dinso.database.entity.ProfileActionGrantEntity;
import se.meepo.dinso.database.repository.DemoProfileRepository;
import se.meepo.dinso.database.repository.ProfileActionGrantRepository;
import se.meepo.dinso.service.CompanyAction;
import se.meepo.dinso.service.CustomerId;
import se.meepo.dinso.service.DemoProfile;

/**
 * Per-person company-action permissions. Independent of {@link se.meepo.dinso.service.DemoRole}
 * (company scoping) and {@link se.meepo.dinso.service.CustomerRules#companyMutations()} (customer
 * variant capability). Designed to hold ~100 actions x ~100 profiles for a scaled-up version of this
 * demo without changing shape.
 */
@Service
@Transactional(readOnly = true)
public class ProfileActionGrantService {
  private final DemoProfileRepository profiles;
  private final ProfileActionGrantRepository grants;

  public ProfileActionGrantService(DemoProfileRepository profiles, ProfileActionGrantRepository grants) {
    this.profiles = profiles;
    this.grants = grants;
  }

  public Set<CompanyAction> grantedActions(DemoProfile profile) {
    return grantedActions(entity(profile.customerId(), profile.id()));
  }

  public Set<CompanyAction> grantedActions(CustomerId customer, String profileExternalId) {
    return grantedActions(entity(customer, profileExternalId));
  }

  /** Throws if the profile does not have the given action granted. */
  public void requireAction(DemoProfile profile, CompanyAction action) {
    if (!grantedActions(profile).contains(action)) {
      throw new SecurityException("Profilen saknar behörighet för åtgärden " + action);
    }
  }

  /** True when the profile has no company actions granted at all — used to block company portal access at login. */
  public boolean hasNoGrants(DemoProfile profile) {
    return grantedActions(profile).isEmpty();
  }

  @Transactional
  public Set<CompanyAction> replaceGrants(CustomerId customer, String profileExternalId, Set<CompanyAction> actions) {
    var entity = entity(customer, profileExternalId);
    // Flush the deletion before inserting the new grants: Hibernate orders inserts before deletes
    // within a flush by default, which would otherwise trip the (profile, action) unique constraint
    // when a grant is replaced with an overlapping action in the same transaction.
    grants.deleteAllInBatch(grants.findByProfile(entity));
    grants.flush();
    var granted = actions == null ? Set.<CompanyAction>of() : Set.copyOf(actions);
    granted.forEach(action -> grants.save(new ProfileActionGrantEntity(entity, action)));
    return granted;
  }

  private Set<CompanyAction> grantedActions(DemoProfileEntity entity) {
    return grants.findByProfile(entity).stream().map(ProfileActionGrantEntity::getAction).collect(java.util.stream.Collectors.toUnmodifiableSet());
  }

  private DemoProfileEntity entity(CustomerId customer, String profileExternalId) {
    return profiles.findByCustomerIdAndExternalId(customer, profileExternalId).orElseThrow(() -> new SecurityException("Unknown demo profile"));
  }
}
