package se.meepo.dinso.service;

import java.util.Set;

/**
 * Sensible starting grants per {@link DemoRole}, used only to seed demo data so existing profiles
 * keep working once per-action permissions are introduced. The admin view can freely override these
 * per person afterwards — a role does not otherwise constrain which actions a profile may hold.
 */
public final class CompanyActionDefaults {
  private CompanyActionDefaults() { }

  public static Set<CompanyAction> forRole(DemoRole role) {
    return switch (role) {
      case COMPANY_ADMIN, SYSTEM_ADMIN -> Set.of(CompanyAction.values());
      case COMPANY_VIEWER -> Set.of(CompanyAction.READ);
      case PRIVATE_CUSTOMER -> Set.of();
    };
  }
}
