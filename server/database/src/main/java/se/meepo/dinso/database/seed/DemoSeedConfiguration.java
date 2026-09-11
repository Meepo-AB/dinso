package se.meepo.dinso.database.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import se.meepo.dinso.database.DemoCatalog;
import se.meepo.dinso.database.entity.DemoProfileEntity;
import se.meepo.dinso.database.repository.DemoProfileRepository;
import se.meepo.dinso.service.CustomerId;

@Configuration
public class DemoSeedConfiguration {
  @Bean CommandLineRunner seedDemoData(CustomerId customer, DemoProfileRepository profiles, DemoDataSeeder dataSeeder) {
    return ignored -> { DemoCatalog.profiles(customer).forEach(profile -> profiles.findByCustomerIdAndExternalId(customer, profile.id()).orElseGet(() -> profiles.save(new DemoProfileEntity(profile)))); dataSeeder.seed(customer); };
  }
}
