package io.github.doriangrelu.lifemanager.vehicle;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.MaintenanceRequest;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.MaintenanceResponse;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.VehicleRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaintenanceIntegrationTest extends IntegrationTestSupport {

    @Inject
    @Client("/")
    HttpClient client;

    HouseholdApi households;
    VehicleApi vehicles;

    @BeforeAll
    void setUp() {
        households = new HouseholdApi(client.toBlocking());
        vehicles = new VehicleApi(client.toBlocking());
    }

    @Test
    void operations_are_recorded_and_listed_newest_first() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var clio = clioOf(owner, household);

        var service = vehicles.record(owner, household, clio,
                new MaintenanceRequest(LocalDate.of(2026, 10, 12), "SERVICE", 45_000, 18_990L, "Garage Martin", null, null, null));
        vehicles.record(owner, household, clio, kind(LocalDate.of(2025, 4, 2), "TIRES"));

        assertThat(service.mileageKm()).isEqualTo(45_000);
        assertThat(service.costCents()).isEqualTo(18_990L);
        assertThat(vehicles.logbook(owner, household, clio)).extracting(MaintenanceResponse::kind)
                .containsExactly("SERVICE", "TIRES");
    }

    @Test
    void invalid_operations_are_refused() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var clio = clioOf(owner, household);
        var tomorrow = LocalDate.now(ZoneId.of("Europe/Paris")).plusDays(1);

        assertThat(failure(() -> vehicles.record(owner, household, clio, kind(tomorrow, "SERVICE"))).code())
                .isEqualTo(400);
        assertThat(failure(() -> vehicles.record(owner, household, clio, kind(LocalDate.of(2026, 1, 1), "WASH")))
                .code()).isEqualTo(400);
        assertThat(failure(() -> vehicles.record(owner, household, clio,
                new MaintenanceRequest(LocalDate.of(2026, 1, 1), "REPAIR", -1, null, null, null, null, null))).code()).isEqualTo(400);
        assertThat(failure(() -> vehicles.record(owner, household, clio,
                new MaintenanceRequest(LocalDate.of(2026, 1, 1), "REPAIR", null, -5L, null, null, null, null))).code()).isEqualTo(400);
    }

    @Test
    void only_other_operations_can_be_specified() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var clio = clioOf(owner, household);
        var day = LocalDate.of(2026, 3, 1);

        var wipers = vehicles.record(owner, household, clio, other(day, "  Essuie-glaces "));
        var plain = vehicles.record(owner, household, clio, other(day, " "));

        assertThat(wipers.otherLabel()).isEqualTo("Essuie-glaces");
        assertThat(plain.otherLabel()).isNull();
        assertThat(vehicles.logbook(owner, household, clio)).extracting(MaintenanceResponse::otherLabel)
                .containsExactlyInAnyOrder("Essuie-glaces", null);
        assertThat(failure(() -> vehicles.record(owner, household, clio,
                new MaintenanceRequest(day, "SERVICE", null, null, null, null, "Pneus hiver", null))).code()).isEqualTo(400);
        assertThat(failure(() -> vehicles.record(owner, household, clio, other(day, "x".repeat(41)))).code())
                .isEqualTo(400);
    }

    @Test
    void an_operation_can_be_deleted_but_only_from_its_own_vehicle() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var clio = clioOf(owner, household);
        var bike = vehicles.register(owner, household, new VehicleRequest("Vélo", null, null, null, null, null)).id();
        var entry = vehicles.record(owner, household, clio, kind(LocalDate.of(2026, 5, 1), "OIL_CHANGE")).id();

        assertThat(failure(() -> vehicles.deleteEntry(owner, household, bike, entry)).code()).isEqualTo(404);
        vehicles.deleteEntry(owner, household, clio, entry);

        assertThat(vehicles.logbook(owner, household, clio)).isEmpty();
        assertThat(failure(() -> vehicles.deleteEntry(owner, household, clio, entry)).code()).isEqualTo(404);
    }

    @Test
    void the_logbook_goes_with_its_vehicle_and_stays_private() {
        var owner = newUser();
        var stranger = newUser();
        var household = households.create(owner, "Maison").id();
        var clio = clioOf(owner, household);
        vehicles.record(owner, household, clio, kind(LocalDate.of(2026, 5, 1), "REPAIR"));

        assertThat(failure(() -> vehicles.logbook(stranger, household, clio)).code()).isEqualTo(404);
        vehicles.remove(owner, household, clio);
        assertThat(failure(() -> vehicles.logbook(owner, household, clio)).code()).isEqualTo(404);
    }

    private UUID clioOf(String owner, UUID household) {
        return vehicles.register(owner, household, new VehicleRequest("Clio", null, null, null, null, null)).id();
    }

    private static MaintenanceRequest kind(LocalDate day, String kind) {
        return new MaintenanceRequest(day, kind, null, null, null, null, null, null);
    }

    private static MaintenanceRequest other(LocalDate day, String label) {
        return new MaintenanceRequest(day, "OTHER", null, null, null, null, label, null);
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
