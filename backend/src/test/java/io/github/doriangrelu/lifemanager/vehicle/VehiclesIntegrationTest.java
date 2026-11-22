package io.github.doriangrelu.lifemanager.vehicle;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.VehicleRequest;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.VehicleResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehiclesIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate MARCH_2027 = LocalDate.of(2027, 3, 1);

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
    void a_vehicle_is_registered_with_an_upper_case_plate_and_shared_in_the_household() {
        var owner = newUser();
        var guest = newUser();
        var household = households.create(owner, "Maison").id();
        households.join(guest, households.issueInvitation(owner, household).code());

        var clio = vehicles.register(owner, household, new VehicleRequest("Clio", "ab-123-cd", MARCH_2027, null, null, null));
        vehicles.register(guest, household, new VehicleRequest("Berlingo", null, null, null, null, null));

        assertThat(clio.licensePlate()).isEqualTo("AB-123-CD");
        assertThat(clio.technicalInspectionDue()).isEqualTo(MARCH_2027);
        assertThat(clio.serviceDue()).isNull();
        assertThat(vehicles.list(guest, household)).extracting(VehicleResponse::name)
                .containsExactly("Berlingo", "Clio");
    }

    @Test
    void invalid_names_are_refused_and_strangers_see_nothing() {
        var owner = newUser();
        var stranger = newUser();
        var household = households.create(owner, "Maison").id();

        assertThat(failure(() -> vehicles.register(owner, household, named(" "))).code()).isEqualTo(400);
        assertThat(failure(() -> vehicles.register(owner, household, named("a".repeat(41)))).code())
                .isEqualTo(400);
        assertThat(failure(() -> vehicles.register(stranger, household, named("Clio"))).code()).isEqualTo(404);
        assertThat(failure(() -> vehicles.list(stranger, household)).code()).isEqualTo(404);
    }

    @Test
    void a_vehicle_can_be_updated_then_removed() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var clio = vehicles.register(owner, household, new VehicleRequest("Clio", null, MARCH_2027, null, null, null)).id();

        var updated = vehicles.update(owner, household, clio,
                new VehicleRequest("Clio V", "AB-123-CD", LocalDate.of(2029, 3, 1), LocalDate.of(2027, 1, 15), null, null));
        vehicles.remove(owner, household, clio);

        assertThat(updated.name()).isEqualTo("Clio V");
        assertThat(updated.technicalInspectionDue()).isEqualTo(LocalDate.of(2029, 3, 1));
        assertThat(updated.serviceDue()).isEqualTo(LocalDate.of(2027, 1, 15));
        assertThat(vehicles.list(owner, household)).isEmpty();
    }

    @Test
    void an_unknown_vehicle_or_a_vehicle_of_another_household_is_not_found() {
        var owner = newUser();
        var neighbour = newUser();
        var household = households.create(owner, "Maison").id();
        var neighbourHousehold = households.create(neighbour, "Chez le voisin").id();
        var neighbourCar = vehicles.register(neighbour, neighbourHousehold, named("Twingo")).id();

        assertThat(failure(() -> vehicles.update(owner, household, neighbourCar, named("Volée"))).code())
                .isEqualTo(404);
        assertThat(failure(() -> vehicles.remove(owner, household, UUID.randomUUID())).code()).isEqualTo(404);
    }

    private static VehicleRequest named(String name) {
        return new VehicleRequest(name, null, null, null, null, null);
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
