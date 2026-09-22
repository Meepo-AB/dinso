package se.meepo.dinso.api;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import se.meepo.dinso.database.DemoSessionService;
import se.meepo.dinso.database.ProfileActionGrantService;
import se.meepo.dinso.database.repository.DemoProfileRepository;
import se.meepo.dinso.service.*;

/**
 * Lets a SYSTEM_ADMIN profile inspect and edit any other demo profile's granted
 * {@link CompanyAction}s for the current customer variant. Available for every customer (system admin
 * profiles exist even for company-less customers like FinBanken), unlike {@link CompanyPortalController}
 * which is only active for customers with a company portal.
 */
@RestController
@RequestMapping("/api/system")
public class SystemAdminController {
  private final DemoSessionService sessions;
  private final ProfileActionGrantService grants;
  private final DemoProfileRepository profiles;
  private final CustomerId customer;

  public SystemAdminController(DemoSessionService sessions, ProfileActionGrantService grants, DemoProfileRepository profiles, CustomerId customer) {
    this.sessions = sessions;
    this.grants = grants;
    this.profiles = profiles;
    this.customer = customer;
  }

  @GetMapping("/profiles")
  List<ProfilePermissions> profiles(HttpServletRequest request) {
    requireSystemAdmin(request);
    return profiles.findByCustomerId(customer).stream()
        .map(profile -> new ProfilePermissions(profile.getExternalId(), profile.getName(), profile.getRole(), profile.getPortal(), profile.getDescription(), grants.grantedActions(customer, profile.getExternalId())))
        .toList();
  }

  @PutMapping("/profiles/{profileId}/actions")
  ProfilePermissions updateActions(HttpServletRequest request, @PathVariable("profileId") String profileId, @RequestBody ActionsRequest input) {
    requireSystemAdmin(request);
    var profile = profiles.findByCustomerIdAndExternalId(customer, profileId).orElseThrow(() -> new IllegalArgumentException("Demoprofilen finns inte"));
    var granted = grants.replaceGrants(customer, profileId, input.actions() == null ? Set.of() : Set.copyOf(input.actions()));
    return new ProfilePermissions(profile.getExternalId(), profile.getName(), profile.getRole(), profile.getPortal(), profile.getDescription(), granted);
  }

  private DemoProfile requireSystemAdmin(HttpServletRequest request) {
    var profile = sessions.requireActive(token(request));
    if (profile.role() != DemoRole.SYSTEM_ADMIN) throw new Forbidden();
    return profile;
  }

  private static String token(HttpServletRequest request) {
    var value = request.getHeader("Authorization");
    if (value == null || !value.startsWith("Bearer ")) throw new Forbidden();
    return value.substring(7);
  }

  public record ActionsRequest(List<CompanyAction> actions) { }
  public record ProfilePermissions(String id, String name, DemoRole role, PortalType portal, String description, Set<CompanyAction> actions) { }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  static class Forbidden extends RuntimeException { }
}
