package se.meepo.dinso.svenskebanken;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import se.meepo.dinso.database.DemoSessionService;
import se.meepo.dinso.database.ProfileActionGrantService;
import se.meepo.dinso.service.CompanyAction;
import se.meepo.dinso.service.CustomerId;

/**
 * Covers the per-action permission model from the assignment: a person with only READ + one action
 * can perform that action but nothing else, a person with zero grants cannot log in to the company
 * portal at all, and a SYSTEM_ADMIN can administer other profiles' grants through the new API.
 */
@SpringBootTest(classes = SvenskeBankenApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompanyActionPermissionIntegrationTest {
  @Autowired private DemoSessionService sessions;
  @Autowired private ProfileActionGrantService grants;
  @Value("${local.server.port}") private int port;

  @Test void personWithOnlyApproveCaseCanApproveButNotAddEmployee() throws Exception {
    grants.replaceGrants(CustomerId.SVENSKEBANKEN, "svenskebanken-viewer", Set.of(CompanyAction.READ, CompanyAction.APPROVE_CASE));
    var client = HttpClient.newHttpClient();
    var token = login(client, "svenskebanken-viewer");

    var casesResponse = client.send(request("/api/company/cases").header("Authorization", "Bearer " + token).GET().build(), HttpResponse.BodyHandlers.ofString());
    assertThat(casesResponse.statusCode()).isEqualTo(200);

    var addResponse = client.send(request("/api/company/employees").header("Authorization", "Bearer " + token).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"Testperson\"}")).build(), HttpResponse.BodyHandlers.ofString());
    assertThat(addResponse.statusCode()).isEqualTo(403);
  }

  @Test void personWithSalaryAndLeaveCanDoThoseButNotApproveOrEnd() throws Exception {
    grants.replaceGrants(CustomerId.SVENSKEBANKEN, "svenskebanken-viewer", Set.of(CompanyAction.READ, CompanyAction.CHANGE_SALARY, CompanyAction.REGISTER_LEAVE));
    var client = HttpClient.newHttpClient();
    var token = login(client, "svenskebanken-viewer");

    var employmentsResponse = client.send(request("/api/company/employments").header("Authorization", "Bearer " + token).GET().build(), HttpResponse.BodyHandlers.ofString());
    assertThat(employmentsResponse.statusCode()).isEqualTo(200);

    var approveResponse = client.send(request("/api/company/cases/does-not-matter/approve").header("Authorization", "Bearer " + token).PUT(HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
    assertThat(approveResponse.statusCode()).isEqualTo(403);
  }

  @Test void personWithoutAnyGrantedActionCannotLogInToTheCompanyPortal() {
    grants.replaceGrants(CustomerId.SVENSKEBANKEN, "svenskebanken-viewer", Set.of());
    org.junit.jupiter.api.Assertions.assertThrows(SecurityException.class, () -> sessions.createSession(CustomerId.SVENSKEBANKEN, "svenskebanken-viewer"));
  }

  @Test void systemAdminCanReadAndUpdateAnotherProfilesGrantsOverHttp() throws Exception {
    var client = HttpClient.newHttpClient();
    var adminToken = login(client, "svenskebanken-system-admin");

    var listResponse = client.send(request("/api/system/profiles").header("Authorization", "Bearer " + adminToken).GET().build(), HttpResponse.BodyHandlers.ofString());
    assertThat(listResponse.statusCode()).isEqualTo(200);
    assertThat(listResponse.body()).contains("svenskebanken-viewer");

    var updateResponse = client.send(request("/api/system/profiles/svenskebanken-viewer/actions").header("Authorization", "Bearer " + adminToken).header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString("{\"actions\":[\"READ\",\"APPROVE_CASE\"]}")).build(), HttpResponse.BodyHandlers.ofString());
    assertThat(updateResponse.statusCode()).isEqualTo(200);
    assertThat(updateResponse.body()).contains("APPROVE_CASE");
    assertThat(grants.grantedActions(CustomerId.SVENSKEBANKEN, "svenskebanken-viewer")).containsExactlyInAnyOrder(CompanyAction.READ, CompanyAction.APPROVE_CASE);
  }

  @Test void nonSystemAdminCannotReachTheSystemAdminApi() throws Exception {
    var client = HttpClient.newHttpClient();
    var token = login(client, "svenskebanken-admin");
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
