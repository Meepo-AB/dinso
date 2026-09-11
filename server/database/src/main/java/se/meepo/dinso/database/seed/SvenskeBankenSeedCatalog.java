package se.meepo.dinso.database.seed;

import java.math.BigDecimal;
import java.util.List;

final class SvenskeBankenSeedCatalog {
  private SvenskeBankenSeedCatalog() { }
  static CustomerSeedCatalog catalog() { return new CustomerSeedCatalog("Nordljus Teknik AB", List.of(new CustomerSeedCatalog.Plan("Flexpension 2024", new BigDecimal("78420.00")), new CustomerSeedCatalog.Plan("ITP 1", new BigDecimal("54870.00"))), new CustomerSeedCatalog.Insurance("Tjänstepension Flex", "FUND", "ACTIVE", new BigDecimal("1284600.00")), new CustomerSeedCatalog.Insurance("Trygg Traditionell", "TRADITIONAL", "ACTIVE", new BigDecimal("842300.00")), new CustomerSeedCatalog.Insurance("Sjuk- och livsskydd", "RISK", "ACTIVE", new BigDecimal("1250000.00")), 28); }
}
