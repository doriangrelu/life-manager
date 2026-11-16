package io.github.doriangrelu.lifemanager.reminder;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.ReminderResponse;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.VehicleRequest;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.VehicleResponse;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/** Parcours complet : les échéances saisies côté véhicules deviennent des rappels côté rappels. */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RemindersIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));

    @Inject
    @Client("/")
    HttpClient client;

    BlockingHttpClient http;
    HouseholdApi households;

    @BeforeAll
    void setUp() {
        http = client.toBlocking();
        households = new HouseholdApi(http);
    }

    @Test
    void overdue_and_close_deadlines_are_reminded_most_urgent_first() {
        var owner = newUser();
        var guest = newUser();
        var household = households.create(owner, "Maison").id();
        households.join(guest, households.issueInvitation(owner, household).code());

        register(owner, household, new VehicleRequest("Clio", null, TODAY.plusDays(15), TODAY.minusDays(46)));
        register(owner, household, new VehicleRequest("Vélo", null, null, TODAY.plusDays(200)));

        assertThat(reminders(guest, household))
                .extracting(ReminderResponse::subject, ReminderResponse::kind, ReminderResponse::status,
                        ReminderResponse::daysLeft)
                .containsExactly(tuple("Clio", "SERVICE", "OVERDUE", -46L),
                        tuple("Clio", "TECHNICAL_INSPECTION", "DUE_SOON", 15L));
    }

    @Test
    void reminders_follow_vehicle_changes_and_removal() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var clio = register(owner, household, new VehicleRequest("Clio", null, TODAY.minusDays(3), null)).id();

        put(owner, household, clio, new VehicleRequest("Clio", null, TODAY.plusYears(2), TODAY.plusDays(5)));
        assertThat(reminders(owner, household)).extracting(ReminderResponse::kind).containsExactly("SERVICE");

        http.exchange(HttpRequest.DELETE(vehicles(household) + "/" + clio).bearerAuth(owner));
        assertThat(reminders(owner, household)).isEmpty();
    }

    @Test
    void strangers_get_no_reminders() {
        var owner = newUser();
        var stranger = newUser();
        var household = households.create(owner, "Maison").id();

        assertThat(reminders(owner, household)).isEmpty();
        assertThat(failure(() -> reminders(stranger, household)).code()).isEqualTo(404);
    }

    private VehicleResponse register(String token, UUID household, VehicleRequest vehicle) {
        return http.retrieve(HttpRequest.POST(vehicles(household), vehicle).bearerAuth(token), VehicleResponse.class);
    }

    private void put(String token, UUID household, UUID vehicle, VehicleRequest changes) {
        http.exchange(HttpRequest.PUT(vehicles(household) + "/" + vehicle, changes).bearerAuth(token));
    }

    private List<ReminderResponse> reminders(String token, UUID household) {
        return http.retrieve(HttpRequest.GET("/api/households/" + household + "/reminders").bearerAuth(token),
                Argument.listOf(ReminderResponse.class));
    }

    private static String vehicles(UUID household) {
        return "/api/households/" + household + "/vehicles";
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
