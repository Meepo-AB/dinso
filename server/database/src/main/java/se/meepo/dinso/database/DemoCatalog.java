package se.meepo.dinso.database;

import java.util.List;
import se.meepo.dinso.service.*;

/** Customer-isolated, deterministic demo profile catalog. */
public final class DemoCatalog {
  private DemoCatalog() { }
  public static List<DemoProfile> profiles(CustomerId customer) {
    var prefix = customer.name().toLowerCase();
    var privateProfiles = List.of(
        new DemoProfile(prefix + "-portfolio", customer, PortalType.PRIVATE, DemoRole.PRIVATE_CUSTOMER, "Elin Berg", "Bred portfölj med fond, skydd och dokument"),
        new DemoProfile(prefix + "-payment", customer, PortalType.PRIVATE, DemoRole.PRIVATE_CUSTOMER, "Oscar Lind Aknar", "Försäkringar och kommande pensionsutbetalningar"));
    var systemAdmin = new DemoProfile(prefix + "-system-admin", customer, PortalType.SYSTEM, DemoRole.SYSTEM_ADMIN, "Alex Lund", "Systemadministratör med full åtkomst till demo-data");
    if (customer == CustomerId.FINBANKEN) return List.of(privateProfiles.get(0), privateProfiles.get(1), systemAdmin);
    var companyName = customer == CustomerId.SVENSKEBANKEN ? "Nordljus Teknik AB" : "Horisont Konsult AB";
    return List.of(privateProfiles.get(0), privateProfiles.get(1), systemAdmin,
        new DemoProfile(prefix + "-admin", customer, PortalType.COMPANY, DemoRole.COMPANY_ADMIN, "Maja Eklund", "Administrerar " + companyName + " med flera planer"),
        new DemoProfile(prefix + "-multi", customer, PortalType.COMPANY, DemoRole.COMPANY_ADMIN, "Norah Sjöberg", "Väljer mellan företag med olika stora bestånd"),
        new DemoProfile(prefix + "-viewer", customer, PortalType.COMPANY, DemoRole.COMPANY_VIEWER, "Linn Åström", "Företagsdata och ärenden med läsbehörighet"));
  }
}
