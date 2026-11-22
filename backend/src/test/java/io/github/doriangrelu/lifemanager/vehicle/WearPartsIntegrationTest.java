package io.github.doriangrelu.lifemanager.vehicle;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Lot Véhicules (v0.16.0) : kilométrage actuel, taille des pneus et pièces d'usure. */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WearPartsIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));
    private static final Argument<Map<String, Object>> JSON = Argument.mapOf(String.class, Object.class);
    private static final Argument<List<Map<String, Object>>> JSON_LIST = Argument.listOf(JSON);

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
    void the_logbook_raises_the_current_mileage_and_parts_show_the_distance_done() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var clio = http.retrieve(HttpRequest.POST(vehicles(household), Map.of("name", "Clio", "mileageKm", 52_000,
                "tireSize", " 205/55  r16 91v ")).bearerAuth(owner), JSON);
        assertThat(clio).containsEntry("tireSize", "205/55 R16 91V").containsEntry("mileageKm", 52_000);
        var vehicle = UUID.fromString(String.valueOf(clio.get("id")));

        // Une opération plus récente relève le kilométrage ; une plus ancienne ne le baisse pas.
        maintenance(owner, household, vehicle, 54_300);
        maintenance(owner, household, vehicle, 40_000);
        assertThat(vehicleOf(owner, household, vehicle)).containsEntry("mileageKm", 54_300);

        var tires = fit(owner, household, vehicle, part("TIRES_FRONT", 40_000, TODAY.minusYears(1), 30_000));
        assertThat(tires).containsEntry("kmDone", 14_300);

        // Nouveau jeu : l'ancien passe dans l'historique avec ses km faits, le kilométrage suit.
        fit(owner, household, vehicle, part("TIRES_FRONT", 60_000, TODAY, null));
        var parts = list(owner, household, vehicle);
        assertThat(parts).hasSize(2);
        assertThat(parts).filteredOn(p -> p.get("removedOn") != null).singleElement()
                .satisfies(p -> assertThat(p).containsEntry("kmDone", 20_000).containsEntry("removedKm", 60_000));
        assertThat(parts).filteredOn(p -> p.get("removedOn") == null).singleElement()
                .satisfies(p -> assertThat(p).containsEntry("kmDone", 0));
        assertThat(vehicleOf(owner, household, vehicle)).containsEntry("mileageKm", 60_000);
    }

    @Test
    void invalid_parts_are_refused_and_households_stay_apart() {
        var owner = newUser();
        var stranger = newUser();
        var household = households.create(owner, "Maison").id();
        households.create(stranger, "Ailleurs");
        var vehicle = UUID.fromString(String.valueOf(http.retrieve(HttpRequest.POST(vehicles(household),
                Map.of("name", "Clio")).bearerAuth(owner), JSON).get("id")));

        assertThat(code(() -> fit(owner, household, vehicle, part("WIPERS", 1_000, TODAY, null))))
                .isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(code(() -> fit(owner, household, vehicle, part("BATTERY", -1, TODAY, null))))
                .isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(code(() -> fit(owner, household, vehicle, part("BATTERY", 1_000, TODAY.plusDays(2), null))))
                .isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        var pads = fit(owner, household, vehicle, part("PADS_FRONT", 30_000, TODAY.minusMonths(6), 40_000));
        // Kilométrage actuel inconnu : la pose le fixe, aucun km parcouru encore.
        assertThat(pads).containsEntry("kmDone", 0);
        assertThat(vehicleOf(owner, household, vehicle)).containsEntry("mileageKm", 30_000);
        // Un remplacement « avant » la pièce montée est refusé.
        assertThat(code(() -> fit(owner, household, vehicle, part("PADS_FRONT", 20_000, TODAY, null))))
                .isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(code(() -> list(stranger, household, vehicle))).isEqualTo(HttpStatus.NOT_FOUND.getCode());

        var padsId = pads.get("id");
        http.exchange(HttpRequest.DELETE(vehicles(household) + "/" + vehicle + "/parts/" + padsId).bearerAuth(owner));
        assertThat(list(owner, household, vehicle)).isEmpty();
    }

    // ---------- outils ----------

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }

    private static Map<String, Object> part(String kind, int fittedKm, LocalDate fittedOn, Integer every) {
        var body = new HashMap<String, Object>();
        body.put("kind", kind);
        body.put("fittedKm", fittedKm);
        body.put("fittedOn", fittedOn.toString());
        body.put("replaceAfterKm", every);
        return body;
    }

    private Map<String, Object> fit(String token, UUID household, UUID vehicle, Map<String, Object> body) {
        return http.retrieve(HttpRequest.POST(vehicles(household) + "/" + vehicle + "/parts", body)
                .bearerAuth(token), JSON);
    }

    private List<Map<String, Object>> list(String token, UUID household, UUID vehicle) {
        return http.retrieve(HttpRequest.GET(vehicles(household) + "/" + vehicle + "/parts").bearerAuth(token),
                JSON_LIST);
    }

    private void maintenance(String token, UUID household, UUID vehicle, int km) {
        http.exchange(HttpRequest.POST(vehicles(household) + "/" + vehicle + "/maintenance",
                Map.of("performedOn", TODAY.toString(), "kind", "OIL_CHANGE", "mileageKm", km)).bearerAuth(token));
    }

    private Map<String, Object> vehicleOf(String token, UUID household, UUID vehicle) {
        return http.retrieve(HttpRequest.GET(vehicles(household)).bearerAuth(token), JSON_LIST).stream()
                .filter(v -> vehicle.toString().equals(v.get("id")))
                .findFirst()
                .orElseThrow();
    }

    private static int code(Runnable call) {
        return failure(call::run).getStatus().getCode();
    }

    private static String vehicles(UUID household) {
        return "/api/households/" + household + "/vehicles";
    }
}
