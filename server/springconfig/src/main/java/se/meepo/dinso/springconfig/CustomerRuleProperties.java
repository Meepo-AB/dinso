package se.meepo.dinso.springconfig;

import java.util.EnumSet;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import se.meepo.dinso.service.CompanyMutation;
import se.meepo.dinso.service.CustomerRules;
import se.meepo.dinso.service.LeaveReason;
import se.meepo.dinso.service.PortalType;

@ConfigurationProperties("dinso.customer")
public class CustomerRuleProperties {
  private Set<PortalType> portals = EnumSet.of(PortalType.PRIVATE);
  private Set<String> locales = Set.of("sv");
  private String defaultLocale = "sv";
  private int maximumFunds;
  private int maximumLeaveMonths;
  private Set<LeaveReason> leaveReasons = EnumSet.noneOf(LeaveReason.class);
  private boolean showFees;
  private boolean showTransactions;
  private boolean showTraditionalBonusRate;
  private Set<CompanyMutation> companyMutations = EnumSet.noneOf(CompanyMutation.class);

  public CustomerRules toRules() { return new CustomerRules(portals, locales, defaultLocale, maximumFunds, maximumLeaveMonths, leaveReasons, showFees, showTransactions, showTraditionalBonusRate, companyMutations); }
  public Set<PortalType> getPortals() { return portals; } public void setPortals(Set<PortalType> portals) { this.portals = portals; }
  public Set<String> getLocales() { return locales; } public void setLocales(Set<String> locales) { this.locales = locales; }
  public String getDefaultLocale() { return defaultLocale; } public void setDefaultLocale(String defaultLocale) { this.defaultLocale = defaultLocale; }
  public int getMaximumFunds() { return maximumFunds; } public void setMaximumFunds(int maximumFunds) { this.maximumFunds = maximumFunds; }
  public int getMaximumLeaveMonths() { return maximumLeaveMonths; } public void setMaximumLeaveMonths(int maximumLeaveMonths) { this.maximumLeaveMonths = maximumLeaveMonths; }
  public Set<LeaveReason> getLeaveReasons() { return leaveReasons; } public void setLeaveReasons(Set<LeaveReason> leaveReasons) { this.leaveReasons = leaveReasons; }
  public boolean isShowFees() { return showFees; } public void setShowFees(boolean showFees) { this.showFees = showFees; }
  public boolean isShowTransactions() { return showTransactions; } public void setShowTransactions(boolean showTransactions) { this.showTransactions = showTransactions; }
  public boolean isShowTraditionalBonusRate() { return showTraditionalBonusRate; } public void setShowTraditionalBonusRate(boolean showTraditionalBonusRate) { this.showTraditionalBonusRate = showTraditionalBonusRate; }
  public Set<CompanyMutation> getCompanyMutations() { return companyMutations; } public void setCompanyMutations(Set<CompanyMutation> companyMutations) { this.companyMutations = companyMutations; }
}
