package se.meepo.dinso.database.seed;

import java.math.BigDecimal;
import java.time.*;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.meepo.dinso.database.entity.*;
import se.meepo.dinso.database.repository.DemoProfileRepository;
import se.meepo.dinso.service.CustomerId;

@Component
public class DemoDataSeeder {
  private final EntityManager entities; private final DemoProfileRepository profiles; private final Clock clock;
  public DemoDataSeeder(EntityManager entities, DemoProfileRepository profiles, Clock clock) { this.entities = entities; this.profiles = profiles; this.clock = clock; }

  @Transactional public void seed(CustomerId customer) {
    if (entities.createQuery("select count(p) from PersonEntity p where p.customerId = :customer", Long.class).setParameter("customer", customer).getSingleResult() > 0) { grantSystemAdminAccess(customer); return; }
    var catalog = CustomerSeedCatalog.forCustomer(customer); var today = LocalDate.now(clock);
    var elin = new PersonEntity(customer, "person-elin", "Elin Berg"); var oscar = new PersonEntity(customer, "person-oscar", "Oscar Lind Aknar"); entities.persist(elin); entities.persist(oscar);
    seedPrivateInsuranceData(customer, elin, catalog, today);
    seedPrivateInsuranceData(customer, oscar, catalog, today);
    entities.persist(new OutPaymentEntity(oscar, today.plusDays(15), new BigDecimal("12640.00"), "UPCOMING")); entities.persist(new OutPaymentEntity(oscar, today.minusDays(16), new BigDecimal("12640.00"), "ONGOING"));
    entities.persist(new DocumentEntity(customer, elin, null, "Årsbesked 2025", "ANNUAL_STATEMENT", today.minusMonths(8))); 
    if (catalog.employeeCount() > 0) seedCompanies(customer, catalog, today, elin);
    entities.persist(new DemoEventEntity(customer, clock.instant(), "SEED_COMPLETE", "Demo-data är klar för " + customer));
    var prefix = customer.name().toLowerCase(); profiles.findByCustomerIdAndExternalId(customer, prefix + "-portfolio").ifPresent(profile -> profile.assignPerson(elin)); profiles.findByCustomerIdAndExternalId(customer, prefix + "-payment").ifPresent(profile -> profile.assignPerson(oscar)); profiles.findByCustomerIdAndExternalId(customer, prefix + "-system-admin").ifPresent(profile -> profile.assignPerson(elin));
  }

  private void grantSystemAdminAccess(CustomerId customer) {
    var profile = profiles.findByCustomerIdAndExternalId(customer, customer.name().toLowerCase() + "-system-admin").orElse(null);
    if (profile == null) return;
    entities.createQuery("select p from PersonEntity p where p.customerId = :customer and p.externalId = :externalId", PersonEntity.class).setParameter("customer", customer).setParameter("externalId", "person-elin").getResultStream().findFirst().ifPresent(profile::assignPerson);
    if (profile.getRole() == se.meepo.dinso.service.DemoRole.SYSTEM_ADMIN && entities.createQuery("select count(a) from CompanyAuthorizationEntity a where a.profile = :profile", Long.class).setParameter("profile", profile).getSingleResult() == 0) entities.createQuery("select c from CompanyEntity c where c.customerId = :customer", CompanyEntity.class).setParameter("customer", customer).getResultStream().forEach(company -> entities.persist(new CompanyAuthorizationEntity(profile, company, profile.getRole())));
  }
  private void seedPrivateInsuranceData(CustomerId customer, PersonEntity person, CustomerSeedCatalog catalog, LocalDate today) {
    var fund = persistInsurance(customer, person, catalog.fundInsurance());
    persistInsurance(customer, person, catalog.traditionalInsurance()); persistInsurance(customer, person, catalog.riskInsurance());
    if (fund != null) {
      entities.persist(new FundHoldingEntity(fund, "Global Index", new BigDecimal("60.00"), catalog.fundInsurance().value().multiply(new BigDecimal("0.60")))); entities.persist(new FundHoldingEntity(fund, "Svenska Aktier", new BigDecimal("40.00"), catalog.fundInsurance().value().multiply(new BigDecimal("0.40"))));
      entities.persist(new TransactionEntity(fund, today.minusDays(3), "Premie från arbetsgivare", new BigDecimal("4850.00"), "BOOKED")); entities.persist(new TransactionEntity(fund, today.minusDays(10), "Fondbyte Global Index", BigDecimal.ZERO, "COMPLETED"));
    }
  }
  private InsuranceEntity persistInsurance(CustomerId customer, PersonEntity person, CustomerSeedCatalog.Insurance insurance) { if (insurance == null) return null; var entity = new InsuranceEntity(customer, person, insurance.productName(), insurance.type(), insurance.status(), insurance.value()); entities.persist(entity); return entity; }
  private void seedCompanies(CustomerId customer, CustomerSeedCatalog catalog, LocalDate today, PersonEntity elin) {
    var primary = seedCompany(customer, "company-primary", catalog.companyName(), catalog, today, catalog.employeeCount(), elin);
    var secondaryName = customer == CustomerId.SVENSKEBANKEN ? "Västhamn Gruppen AB" : "Horisont Gruppen AB";
    var secondary = seedCompany(customer, "company-secondary", secondaryName, catalog, today, 3, null);
    profiles.findByCustomerId(customer).stream().filter(profile -> profile.getPortal().name().equals("COMPANY") || profile.getRole() == se.meepo.dinso.service.DemoRole.SYSTEM_ADMIN).forEach(profile -> {
      entities.persist(new CompanyAuthorizationEntity(profile, primary, profile.getRole()));
      if (profile.getExternalId().endsWith("-multi") || profile.getRole() == se.meepo.dinso.service.DemoRole.SYSTEM_ADMIN) entities.persist(new CompanyAuthorizationEntity(profile, secondary, profile.getRole()));
    });
  }
  private CompanyEntity seedCompany(CustomerId customer, String externalId, String companyName, CustomerSeedCatalog catalog, LocalDate today, int employeeCount, PersonEntity firstEmployee) {
    var company = new CompanyEntity(customer, externalId, companyName); entities.persist(company);
    var plans = catalog.plans().stream().map(plan -> { var planName = externalId.equals("company-secondary") ? plan.name() + " Grupp" : plan.name(); var entity = new PensionPlanEntity(customer, company, planName, plan.monthlyPremium()); entities.persist(entity); return entity; }).toList();
    for (int number = 1; number <= employeeCount; number++) { var person = number == 1 && firstEmployee != null ? firstEmployee : new PersonEntity(customer, externalId + "-employee-" + number, employeeName(number)); if (person != firstEmployee) entities.persist(person); entities.persist(new EmploymentEntity(customer, person, company, plans.get(number % plans.size()), BigDecimal.valueOf(36000L + number * 1200L), today.minusMonths(number + 2L), employmentStatus(number))); }
    entities.persist(new DocumentEntity(customer, null, company, "Faktura september", "INVOICE", today.minusDays(2))); entities.persist(new InvoiceEntity(company, today.plusDays(20), new BigDecimal("133290.00"), "OPEN"));
    entities.persist(new CompanyCaseEntity(customer, company, "Löneändring", "PENDING", today.plusDays(7), "En registrerad löneändring väntar på granskning."));
    entities.persist(new CompanyCaseEntity(customer, company, "Tjänstledighet", "ONGOING", today.plusDays(19), "En tjänstledighet är under handläggning."));
    entities.persist(new CompanyCaseEntity(customer, company, "Nyanslutning", "COMPLETED", today.plusDays(9), "Anslutningen är komplett och redo för nästa premieflöde."));
    return company;
  }
  private static String employeeName(int number) { return new String[] { "Alva Norberg", "Mio Sten", "Tilde Rask", "Hugo Dahl", "Nora Holst", "Ivar Holm", "Saga Mark", "Leo Nyberg" }[(number - 1) % 8] + " " + number; }
  private static String employmentStatus(int number) { return switch (number % 4) { case 0 -> "UPCOMING"; case 1 -> "ACTIVE"; case 2 -> "LEAVE"; default -> "ENDED"; }; }
}
