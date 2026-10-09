package se.meepo.dinso.api.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import se.meepo.dinso.database.DemoSessionService;
import se.meepo.dinso.service.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final DemoSessionService sessions;
  private final CustomerId customer;

  public AuthController(DemoSessionService sessions, CustomerId customer) {
    this.sessions = sessions;
    this.customer = customer;
  }

  @PostMapping("/login")
  LoginResponse login(@RequestBody LoginRequest request) {
    var profile = sessions.login(customer, request.profileId());
    return new LoginResponse(sessions.createSession(customer, request.profileId()), profile);
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void logout(HttpServletRequest request) {
    sessions.logout(token(request));
  }

  private static String token(HttpServletRequest request) {
    var value = request.getHeader("Authorization");
    if (value == null || !value.startsWith("Bearer ")) throw new Unauthorized();
    return value.substring(7);
  }

  public record LoginRequest(String profileId) {}

  public record LoginResponse(String token, DemoProfile profile) {}

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  static class Unauthorized extends RuntimeException {}
}
