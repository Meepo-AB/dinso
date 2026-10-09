package se.meepo.dinso.database;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.meepo.dinso.database.entity.*;
import se.meepo.dinso.database.repository.*;
import se.meepo.dinso.service.CustomerRules;
import se.meepo.dinso.service.DemoProfile;

@Service
@Transactional(readOnly = true)
public class PrivatePortalDataService {
  private final DemoProfileRepository profiles;
  private final InsuranceRepository insurances;
  private final FundHoldingRepository holdings;
  private final TransactionRepository transactions;
  private final DocumentRepository documents;
  private final OutPaymentRepository payments;
  private final CustomerRules rules;
  private final Clock clock;

  public PrivatePortalDataService(
      DemoProfileRepository profiles,
      InsuranceRepository insurances,
      FundHoldingRepository holdings,
      TransactionRepository transactions,
      DocumentRepository documents,
      OutPaymentRepository payments,
      CustomerRules rules,
      Clock clock) {
    this.profiles = profiles;
    this.insurances = insurances;
    this.holdings = holdings;
    this.transactions = transactions;
    this.documents = documents;
    this.payments = payments;
    this.rules = rules;
    this.clock = clock;
  }

  public Overview overview(DemoProfile profile) {
    var person = person(profile);
    var items = insurances.findByPerson(person).stream().map(this::insurance).toList();
    var total =
        items.stream()
            .map(item -> new BigDecimal(item.value()))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .toPlainString();
    return new Overview(person.getDisplayName(), total, items);
  }

  public InsuranceDetail insurance(DemoProfile profile, String insuranceId) {
    return insuranceDetail(ownedInsurance(person(profile), insuranceId));
  }

  public List<Transaction> transactions(DemoProfile profile, String status) {
    var person = person(profile);
    return transactions.findByInsuranceIn(insurances.findByPerson(person)).stream()
        .filter(
            item -> status == null || status.isBlank() || item.getStatus().equalsIgnoreCase(status))
        .sorted(Comparator.comparing(TransactionEntity::getBookedOn).reversed())
        .map(
            item ->
                new Transaction(
                    item.getInsurance().getId(),
                    item.getBookedOn().toString(),
                    item.getDescription(),
                    item.getAmount().toPlainString(),
                    item.getStatus()))
        .toList();
  }

  public List<Document> documents(DemoProfile profile) {
    return documents.findByPerson(person(profile)).stream()
        .sorted(Comparator.comparing(DocumentEntity::getPublishedOn).reversed())
        .map(
            item -> new Document(item.getTitle(), item.getType(), item.getPublishedOn().toString()))
        .toList();
  }

  public List<OutPayment> payments(DemoProfile profile) {
    return payments.findByPerson(person(profile)).stream()
        .sorted(Comparator.comparing(OutPaymentEntity::getPaymentDate))
        .map(
            item ->
                new OutPayment(
                    item.getPaymentDate().toString(),
                    item.getGrossAmount().toPlainString(),
                    item.getStatus()))
        .toList();
  }

  @Transactional
  public InsuranceDetail updateFundAllocation(
      DemoProfile profile, String insuranceId, List<AllocationInput> allocation) {
    var insurance = ownedInsurance(person(profile), insuranceId);
    if (!"FUND".equals(insurance.getType()))
      throw new IllegalArgumentException("Fondfördelning kan bara ändras för fondförsäkringar");
    if (allocation == null || allocation.isEmpty() || allocation.size() > rules.maximumFunds())
      throw new IllegalArgumentException(
          "Fördelningen måste innehålla mellan 1 och " + rules.maximumFunds() + " fonder");
    if (allocation.stream()
            .map(AllocationInput::fundName)
            .anyMatch(name -> name == null || name.isBlank())
        || allocation.stream()
                .map(input -> input.fundName().trim().toLowerCase())
                .distinct()
                .count()
            != allocation.size())
      throw new IllegalArgumentException("Varje fond måste ha ett unikt namn");
    var total =
        allocation.stream().map(AllocationInput::percent).reduce(BigDecimal.ZERO, BigDecimal::add);
    if (allocation.stream()
            .anyMatch(input -> input.percent() == null || input.percent().signum() < 0)
        || total.compareTo(new BigDecimal("100")) != 0)
      throw new IllegalArgumentException(
          "Fondandelarna måste vara positiva och summera till 100 procent");
    holdings.deleteByInsurance(insurance);
    var totalValue = insurance.getValue();
    allocation.forEach(
        input ->
            holdings.save(
                new FundHoldingEntity(
                    insurance,
                    input.fundName().trim(),
                    input.percent(),
                    totalValue.multiply(input.percent()).movePointLeft(2))));
    return insuranceDetail(insurance);
  }

  private PersonEntity person(DemoProfile profile) {
    var entity =
        profiles
            .findByCustomerIdAndExternalId(profile.customerId(), profile.id())
            .orElseThrow(() -> new SecurityException("Unknown demo profile"));
    if (entity.getPerson() == null)
      throw new SecurityException("Profile has no private data access");
    return entity.getPerson();
  }

  private InsuranceEntity ownedInsurance(PersonEntity person, String insuranceId) {
    var insurance =
        insurances
            .findById(insuranceId)
            .orElseThrow(() -> new IllegalArgumentException("Försäkringen finns inte"));
    if (!insurance.getPerson().getId().equals(person.getId()))
      throw new SecurityException("Försäkringen tillhör inte den aktiva profilen");
    return insurance;
  }

  private Insurance insurance(InsuranceEntity item) {
    return new Insurance(
        item.getId(),
        item.getProductName(),
        item.getType(),
        item.getStatus(),
        item.getValue().toPlainString());
  }

  private InsuranceDetail insuranceDetail(InsuranceEntity item) {
    var fundHoldings =
        holdings.findByInsurance(item).stream()
            .map(
                holding ->
                    new FundHolding(
                        holding.getFundName(),
                        holding.getAllocationPercent().toPlainString(),
                        holding.getMarketValue().toPlainString()))
            .toList();
    return new InsuranceDetail(
        insurance(item),
        fundHoldings,
        "FUND".equals(item.getType())
            ? List.of("Återbetalningsskydd")
            : List.of("Efterlevandeskydd"),
        "FUND".equals(item.getType()));
  }

  public record Overview(String personName, String totalValue, List<Insurance> insurances) {}

  public record Insurance(
      String id, String productName, String type, String status, String value) {}

  public record InsuranceDetail(
      Insurance insurance,
      List<FundHolding> fundHoldings,
      List<String> benefits,
      boolean hasRefundProtection) {}

  public record FundHolding(String fundName, String allocationPercent, String marketValue) {}

  public record Transaction(
      String insuranceId, String bookedOn, String description, String amount, String status) {}

  public record Document(String title, String type, String publishedOn) {}

  public record OutPayment(String paymentDate, String grossAmount, String status) {}

  public record AllocationInput(String fundName, BigDecimal percent) {}
}
