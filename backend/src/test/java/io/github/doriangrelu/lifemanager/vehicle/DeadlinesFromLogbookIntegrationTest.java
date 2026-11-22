package io.github.doriangrelu.lifemanager.vehicle;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.ReminderResponse;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.MaintenanceRequest;
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

/** Un contrôle technique ou une révision saisi au carnet reporte l'échéance du véhicule. */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeadlinesFromLogbookIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));
    private static final LocalDate LAST_YEAR = TODAY.minusYears(1);

    @Inject
    @Client("/")
    HttpClient client;

    BlockingHttpClient http;
    HouseholdApi households;
    VehicleApi vehicles;

    @BeforeAll
    void setUp() {
        http = client.toBlocking();
        households = new HouseholdApi(http);
        vehicles = new VehicleApi(http);
    }

    @Test
    void a_technical_inspection_sets_the_next_one_2_years_later() {
        var garage = newGarage(null, LAST_YEAR);

        record(garage, TODAY, "TECHNICAL_INSPECTION", null);

        assertThat(vehicle(garage).technicalInspectionDue()).isEqualTo(TODAY.plusYears(2));
        assertThat(vehicle(garage).serviceDue()).isEqualTo(LAST_YEAR);
    }

    @Test
    void a_service_sets_the_next_one_1_year_later() {
        var garage = newGarage(LAST_YEAR, null);

        record(garage, TODAY, "SERVICE", null);

        assertThat(vehicle(garage).serviceDue()).isEqualTo(TODAY.plusYears(1));
        assertThat(vehicle(garage).technicalInspectionDue()).isEqualTo(LAST_YEAR);
    }

    @Test
    void a_precise_date_wins() {
        var garage = newGarage(null, null);
        var recheck = TODAY.plusMonths(2);

        record(garage, TODAY, "TECHNICAL_INSPECTION", recheck);

        assertThat(vehicle(garage).technicalInspectionDue()).isEqualTo(recheck);
    }

    @Test
    void an_older_inspection_does_not_move_the_deadline_back() {
        var garage = newGarage(null, null);
        record(garage, TODAY, "TECHNICAL_INSPECTION", null);

        record(garage, TODAY.minusYears(2), "TECHNICAL_INSPECTION", null);

        assertThat(vehicle(garage).technicalInspectionDue()).isEqualTo(TODAY.plusYears(2));
        assertThat(vehicles.logbook(garage.owner, garage.household, garage.vehicle)).hasSize(2);
    }

    @Test
    void invalid_precise_dates_are_refused_and_nothing_is_saved() {
        var garage = newGarage(null, null);

        assertThat(failure(() -> record(garage, TODAY, "TECHNICAL_INSPECTION", TODAY)).code()).isEqualTo(400);
        assertThat(failure(() -> record(garage, TODAY, "OIL_CHANGE", TODAY.plusYears(1))).code()).isEqualTo(400);
        assertThat(vehicles.logbook(garage.owner, garage.household, garage.vehicle)).isEmpty();
        assertThat(vehicle(garage).technicalInspectionDue()).isNull();
    }

    @Test
    void reminders_follow_the_new_deadline() {
        var garage = newGarage(TODAY.minusDays(3), null);
        assertThat(reminders(garage)).extracting(ReminderResponse::kind).containsExactly("TECHNICAL_INSPECTION");

        record(garage, TODAY, "TECHNICAL_INSPECTION", null);

        assertThat(reminders(garage)).isEmpty();
    }

    /** Un foyer et son véhicule. */
    private record Garage(String owner, UUID household, UUID vehicle) {
    }

    private Garage newGarage(LocalDate technicalInspectionDue, LocalDate serviceDue) {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var vehicle = vehicles.register(owner, household,
                new VehicleRequest("Clio", null, technicalInspectionDue, serviceDue, null, null)).id();
        return new Garage(owner, household, vehicle);
    }

    private void record(Garage garage, LocalDate day, String kind, LocalDate nextDueOn) {
        vehicles.record(garage.owner, garage.household, garage.vehicle,
                new MaintenanceRequest(day, kind, null, null, null, nextDueOn, null, null));
    }

    private VehicleResponse vehicle(Garage garage) {
        return vehicles.list(garage.owner, garage.household).getFirst();
    }

    private List<ReminderResponse> reminders(Garage garage) {
        return http.retrieve(HttpRequest.GET("/api/households/" + garage.household + "/reminders")
                .bearerAuth(garage.owner), Argument.listOf(ReminderResponse.class));
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
