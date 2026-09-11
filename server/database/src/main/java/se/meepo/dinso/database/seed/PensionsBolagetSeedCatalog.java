package se.meepo.dinso.database.seed;

import java.math.BigDecimal;
import java.util.List;

final class PensionsBolagetSeedCatalog {
  private PensionsBolagetSeedCatalog() { }
  static CustomerSeedCatalog catalog() { return new CustomerSeedCatalog("Horisont Konsult AB", List.of(new CustomerSeedCatalog.Plan("Tjänstepension 2018", new BigDecimal("106240.00")), new CustomerSeedCatalog.Plan("Livslång pension", new BigDecimal("87930.00"))), new CustomerSeedCatalog.Insurance("Tjänstepension 2018", "FUND", "ACTIVE", new BigDecimal("1943200.00")), new CustomerSeedCatalog.Insurance("Livsvarig pension", "TRADITIONAL", "PAYING", new BigDecimal("8940.00")), new CustomerSeedCatalog.Insurance("Familjeskydd", "RISK", "ACTIVE", new BigDecimal("980000.00")), 34); }
}
