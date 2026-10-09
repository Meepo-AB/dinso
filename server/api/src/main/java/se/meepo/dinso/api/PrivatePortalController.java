package se.meepo.dinso.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import se.meepo.dinso.database.DemoSessionService;
import se.meepo.dinso.database.PrivatePortalDataService;
import se.meepo.dinso.service.*;

@RestController
@RequestMapping("/api/private")
public class PrivatePortalController {
  private final DemoSessionService sessions;
  private final PrivatePortalDataService data;

  public PrivatePortalController(DemoSessionService sessions, PrivatePortalDataService data) {
    this.sessions = sessions;
    this.data = data;
  }

  @GetMapping("/overview")
  PrivatePortalDataService.Overview overview(HttpServletRequest request) {
    return data.overview(current(request));
  }

  @GetMapping("/insurances/{insuranceId}")
  PrivatePortalDataService.InsuranceDetail insurance(
      HttpServletRequest request, @PathVariable("insuranceId") String insuranceId) {
    return data.insurance(current(request), insuranceId);
  }

  @GetMapping("/transactions")
  java.util.List<PrivatePortalDataService.Transaction> transactions(
      HttpServletRequest request, @RequestParam(name = "status", required = false) String status) {
    return data.transactions(current(request), status);
  }

  @GetMapping("/documents")
  java.util.List<PrivatePortalDataService.Document> documents(HttpServletRequest request) {
    return data.documents(current(request));
  }

  @GetMapping("/payments")
  java.util.List<PrivatePortalDataService.OutPayment> payments(HttpServletRequest request) {
    return data.payments(current(request));
  }

  @PutMapping("/insurances/{insuranceId}/fund-allocation")
  PrivatePortalDataService.InsuranceDetail updateFundAllocation(
      HttpServletRequest request,
      @PathVariable("insuranceId") String insuranceId,
      @RequestBody FundAllocationRequest input) {
    return data.updateFundAllocation(current(request), insuranceId, input.allocation());
  }

  private DemoProfile current(HttpServletRequest request) {
    var profile = sessions.requireActive(token(request));
    if (profile.portal() != PortalType.PRIVATE && profile.role() != DemoRole.SYSTEM_ADMIN)
      throw new Forbidden();
    return profile;
  }

  private static String token(HttpServletRequest request) {
    var value = request.getHeader("Authorization");
    if (value == null || !value.startsWith("Bearer ")) throw new Forbidden();
    return value.substring(7);
  }

  public record FundAllocationRequest(
      java.util.List<PrivatePortalDataService.AllocationInput> allocation) {}

  @ResponseStatus(HttpStatus.FORBIDDEN)
  static class Forbidden extends RuntimeException {}
}
