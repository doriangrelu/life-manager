package io.github.doriangrelu.lifemanager.vehicle;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.github.doriangrelu.lifemanager.vehicle.contract.event.VehicleDeadlinesChangedV1;
import io.github.doriangrelu.lifemanager.vehicle.contract.event.VehicleRemovedV1;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.VehicleRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleContractEventsTest extends IntegrationTestSupport {

    /** Mémorise les événements de contrat du véhicule, comme le ferait un autre domaine. */
    @Singleton
    static class ContractEvents {

        final List<Object> received = new CopyOnWriteArrayList<>();

        @EventListener
        void on(VehicleDeadlinesChangedV1 event) {
            received.add(event);
        }

        @EventListener
        void on(VehicleRemovedV1 event) {
            received.add(event);
        }
    }

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    ContractEvents contractEvents;

    @Test
    void registering_updating_and_removing_a_vehicle_are_announced() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        var household = new HouseholdApi(client.toBlocking()).create(token, "Maison").id();
        var vehicles = new VehicleApi(client.toBlocking());

        var clio = vehicles.register(token, household, new VehicleRequest("Clio", null, null, null)).id();
        vehicles.update(token, household, clio, new VehicleRequest("Clio", null, null, LocalDate.of(2027, 1, 15)));
        failure(() -> vehicles.update(token, household, clio, new VehicleRequest(" ", null, null, null)));
        vehicles.remove(token, household, clio);

        assertThat(contractEvents.received).filteredOn(event -> vehicleOf(event).equals(clio)).satisfiesExactly(
                registered -> assertThat(((VehicleDeadlinesChangedV1) registered).serviceDue()).isNull(),
                updated -> assertThat(((VehicleDeadlinesChangedV1) updated).serviceDue())
                        .isEqualTo(LocalDate.of(2027, 1, 15)),
                removed -> assertThat(removed).isInstanceOf(VehicleRemovedV1.class));
    }

    private static UUID vehicleOf(Object event) {
        return switch (event) {
            case VehicleDeadlinesChangedV1 changed -> changed.vehicleId();
            case VehicleRemovedV1 removed -> removed.vehicleId();
            default -> throw new IllegalArgumentException("événement inattendu : " + event);
        };
    }
}
