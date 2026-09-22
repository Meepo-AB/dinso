package se.meepo.dinso.service;

/**
 * Per-person, per-action permissions in the company portal. Independent of
 * {@link DemoRole} (which scopes *which* companies a profile may act on) and
 * of {@link CustomerRules#companyMutations()} (which scopes what a customer
 * *variant* supports at all). A demo profile is granted zero or more of
 * these; {@code READ} itself is required to log in to the company portal.
 */
public enum CompanyAction {
  READ,
  APPROVE_CASE,
  ADD_EMPLOYEE,
  CHANGE_SALARY,
  REGISTER_LEAVE,
  END_EMPLOYMENT
}
