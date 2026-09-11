package se.meepo.dinso.api;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.meepo.dinso.database.repository.DemoProfileRepository;
import se.meepo.dinso.service.CustomerId;
import se.meepo.dinso.service.CustomerRules;
import se.meepo.dinso.service.DemoProfile;

@RestController
@RequestMapping("/api/demo")
public class DemoProfileController {
  private final CustomerId customerId;
  private final CustomerRules rules;
  private final DemoProfileRepository profiles;
  public DemoProfileController(CustomerId customerId, CustomerRules rules, DemoProfileRepository profiles) { this.customerId = customerId; this.rules = rules; this.profiles = profiles; }
  @GetMapping("/profiles") public List<DemoProfile> profiles() { return profiles.findByCustomerId(customerId).stream().map(profile -> profile.toDomain()).toList(); }
  @GetMapping("/config") public CustomerRules config() { return rules; }
  @GetMapping("/health") public String health() { return "Dinso " + customerId + " is ready"; }
}
