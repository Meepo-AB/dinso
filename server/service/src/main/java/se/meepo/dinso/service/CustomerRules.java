package se.meepo.dinso.service;

import java.util.Set;

/** Rules that have an observable effect in a customer demo. */
public record CustomerRules(
    Set<PortalType> portals,
    Set<String> locales,
    String defaultLocale,
    int maximumFunds,
    int maximumLeaveMonths,
    Set<LeaveReason> leaveReasons,
    boolean showFees,
    boolean showTransactions,
    boolean showTraditionalBonusRate,
    Set<CompanyMutation> companyMutations
) {
  public CustomerRules {
    portals = Set.copyOf(portals);
    locales = Set.copyOf(locales);
    leaveReasons = Set.copyOf(leaveReasons);
    companyMutations = Set.copyOf(companyMutations);
    if (portals.isEmpty() || locales.isEmpty() || !locales.contains(defaultLocale)) throw new IllegalArgumentException("Customer rules require supported portals and a valid default locale");
    if (maximumFunds < 1 || maximumLeaveMonths < 0) throw new IllegalArgumentException("Customer limits must not be negative");
    if (!portals.contains(PortalType.COMPANY) && (!companyMutations.isEmpty() || maximumLeaveMonths != 0 || !leaveReasons.isEmpty())) throw new IllegalArgumentException("Private-only customers cannot have company rules");
  }
}
