package se.meepo.dinso.database.seed;

import java.math.BigDecimal;
import java.util.List;

final class FinBankenSeedCatalog {
  private FinBankenSeedCatalog() { }
  static CustomerSeedCatalog catalog() { return new CustomerSeedCatalog("FinBanken Demo Ltd", List.of(), new CustomerSeedCatalog.Insurance("Global Equity Select", "FUND", "ACTIVE", new BigDecimal("1506800.00")), null, new CustomerSeedCatalog.Insurance("Income protection", "RISK", "ACTIVE", new BigDecimal("850000.00")), 0); }
}
