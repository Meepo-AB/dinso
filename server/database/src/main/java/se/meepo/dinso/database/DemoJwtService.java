package se.meepo.dinso.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.meepo.dinso.service.CustomerId;

@Service
public class DemoJwtService {
  private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
  private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
  private final byte[] secret;
  private final Clock clock;

  public DemoJwtService(@Value("${dinso.security.jwt-secret}") String secret, Clock clock) {
    this.secret = secret.getBytes(StandardCharsets.UTF_8);
    this.clock = clock;
  }

  public String issue(String sessionId, CustomerId customer, Instant expiresAt) {
    var header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
    var payload =
        encode(
            "{\"jti\":\""
                + sessionId
                + "\",\"customer\":\""
                + customer.name()
                + "\",\"iat\":"
                + clock.instant().getEpochSecond()
                + ",\"exp\":"
                + expiresAt.getEpochSecond()
                + "}");
    var signed = header + "." + payload;
    return signed + "." + sign(signed);
  }

  public Claims verify(String token) {
    var parts = token.split("\\.");
    if (parts.length != 3
        || !MessageDigest.isEqual(
            sign(parts[0] + "." + parts[1]).getBytes(StandardCharsets.US_ASCII),
            parts[2].getBytes(StandardCharsets.US_ASCII)))
      throw new SecurityException("Invalid demo token");
    try {
      var payload = new String(DECODER.decode(parts[1]), StandardCharsets.UTF_8);
      var sessionId = stringClaim(payload, "jti");
      var customer = CustomerId.valueOf(stringClaim(payload, "customer"));
      var expiration = Long.parseLong(numberClaim(payload, "exp"));
      if (Instant.ofEpochSecond(expiration).isBefore(clock.instant()))
        throw new SecurityException("Demo token has expired");
      return new Claims(sessionId, customer, Instant.ofEpochSecond(expiration));
    } catch (IllegalArgumentException exception) {
      throw new SecurityException("Invalid demo token");
    }
  }

  private String sign(String value) {
    try {
      var mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret, "HmacSHA256"));
      return ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.US_ASCII)));
    } catch (Exception exception) {
      throw new IllegalStateException("Cannot sign demo token", exception);
    }
  }

  private static String encode(String value) {
    return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
  }

  private static String stringClaim(String json, String claim) {
    var value = json.replaceAll(".*\\\"" + claim + "\\\":\\\"([^\\\"]+)\\\".*", "$1");
    if (value.equals(json)) throw new IllegalArgumentException("Missing claim");
    return value;
  }

  private static String numberClaim(String json, String claim) {
    var value = json.replaceAll(".*\\\"" + claim + "\\\":([0-9]+).*", "$1");
    if (value.equals(json)) throw new IllegalArgumentException("Missing claim");
    return value;
  }

  public record Claims(String sessionId, CustomerId customer, Instant expiresAt) {}
}
