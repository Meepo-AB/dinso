package se.meepo.dinso.database.seed;

import java.math.BigDecimal;
import java.util.List;
import se.meepo.dinso.service.CustomerId;

public record CustomerSeedCatalog(
    String companyName,
    List<Plan> plans,
    Insurance fundInsurance,
    Insurance traditionalInsurance,
    Insurance riskInsurance,
    int employeeCount
) {
  public record Plan(String name, BigDecimal monthlyPremium) { }
  public record Insurance(String productName, String type, String status, BigDecimal value) { }

  public static CustomerSeedCatalog forCustomer(CustomerId customer) {
    return switch (customer) {
      case SVENSKEBANKEN -> SvenskeBankenSeedCatalog.catalog();
      case PENSIONSBOLAGET -> PensionsBolagetSeedCatalog.catalog();
      case FINBANKEN -> FinBankenSeedCatalog.catalog();
    };
  }
}
