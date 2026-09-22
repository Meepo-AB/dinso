package se.meepo.dinso.database.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import se.meepo.dinso.database.DemoCatalog;
import se.meepo.dinso.database.ProfileActionGrantService;
import se.meepo.dinso.database.entity.DemoProfileEntity;
import se.meepo.dinso.database.repository.DemoProfileRepository;
import se.meepo.dinso.service.CompanyActionDefaults;
import se.meepo.dinso.service.CustomerId;

@Configuration
public class DemoSeedConfiguration {
  @Bean CommandLineRunner seedDemoData(CustomerId customer, DemoProfileRepository profiles, DemoDataSeeder dataSeeder, ProfileActionGrantService grants) {
    return ignored -> {
      DemoCatalog.profiles(customer).forEach(profile -> {
        var existing = profiles.findByCustomerIdAndExternalId(customer, profile.id());
        if (existing.isPresent()) return;
        var saved = profiles.save(new DemoProfileEntity(profile));
        grants.replaceGrants(customer, saved.getExternalId(), CompanyActionDefaults.forRole(profile.role()));
      });
      dataSeeder.seed(customer);
    };
  }
}
