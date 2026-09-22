package se.meepo.dinso.finbanken;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * FinBanken has no company portal ({@code dinso.customer.portals=PRIVATE}), so
 * {@code CompanyActionPermissionIntegrationTest} (svenskebanken/pensionsbolaget) does not apply
 * here. This instead confirms {@code SystemAdminController} is still reachable for a
 * company-less customer variant — system admin profiles exist for every customer — and that a
 * non-admin profile is rejected.
 */
@SpringBootTest(classes = FinBankenApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SystemAdminPermissionIntegrationTest {
  @Value("${local.server.port}") private int port;

  @Test void systemAdminCanListProfilesEvenWithoutACompanyPortal() throws Exception {
    var client = HttpClient.newHttpClient();
    var token = login(client, "finbanken-system-admin");
    var response = client.send(request("/api/system/profiles").header("Authorization", "Bearer " + token).GET().build(), HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("finbanken-portfolio");
  }

  @Test void nonSystemAdminCannotReachTheSystemAdminApi() throws Exception {
    var client = HttpClient.newHttpClient();
    var token = login(client, "finbanken-portfolio");
    var response = client.send(request("/api/system/profiles").header("Authorization", "Bearer " + token).GET().build(), HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(403);
  }

  private HttpRequest.Builder request(String path) { return HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)); }

  private String login(HttpClient client, String profileId) throws Exception {
    var response = client.send(request("/api/auth/login").header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString("{\"profileId\":\"" + profileId + "\"}")).build(), HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(200);
    return response.body().replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
  }
}
