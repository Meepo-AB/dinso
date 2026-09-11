package se.meepo.dinso.pensionsbolaget;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.meepo.dinso.database.CompanyPortalDataService;
import se.meepo.dinso.database.DemoSessionService;
import se.meepo.dinso.service.CompanyMutation;
import se.meepo.dinso.service.CustomerId;
import se.meepo.dinso.service.CustomerRules;
import se.meepo.dinso.service.LeaveReason;

@SpringBootTest(classes = PensionsBolagetApplication.class)
class PensionsBolagetRulesIntegrationTest {
  @Autowired private CustomerRules rules;
  @Autowired private CompanyPortalDataService companyData;
  @Autowired private DemoSessionService sessions;
  @Test void loadsPensionsBolagetRules() { assertThat(rules.maximumFunds()).isEqualTo(5); assertThat(rules.maximumLeaveMonths()).isEqualTo(12); assertThat(rules.leaveReasons()).containsExactly(LeaveReason.PARENTAL_LEAVE); assertThat(rules.companyMutations()).contains(CompanyMutation.END_EMPLOYMENT); }
  @Test void persistsApprovedCompanyCases() {
    var profile = sessions.requireActive(sessions.createSession(CustomerId.PENSIONSBOLAGET, "pensionsbolaget-admin"));
    var company = companyData.companies(profile).getFirst();
    var pending = companyData.cases(profile, company.id()).stream().filter(item -> item.status().equals("PENDING")).findFirst().orElseThrow();
    companyData.approveCase(profile, company.id(), pending.id());
    assertThat(companyData.cases(profile, company.id())).anySatisfy(item -> { assertThat(item.id()).isEqualTo(pending.id()); assertThat(item.status()).isEqualTo("APPROVED"); });
  }
  @Test void givesNorahAccessToBothSeededEmployers() {
    var profile = sessions.requireActive(sessions.createSession(CustomerId.PENSIONSBOLAGET, "pensionsbolaget-multi"));
    var companies = companyData.companies(profile);
    assertThat(companies).extracting(CompanyPortalDataService.Company::name).containsExactlyInAnyOrder("Horisont Gruppen AB", "Horisont Konsult AB");
    var group = companies.stream().filter(company -> company.name().equals("Horisont Gruppen AB")).findFirst().orElseThrow();
    assertThat(companyData.plans(profile, group.id())).extracting(CompanyPortalDataService.Plan::name).allMatch(name -> name.endsWith(" Grupp"));
  }
}
