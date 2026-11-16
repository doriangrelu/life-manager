package io.github.doriangrelu.lifemanager.household;

import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdCreatedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MemberJoinedV1;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HouseholdContractEventsTest extends IntegrationTestSupport {

    /** Mémorise les événements de contrat du foyer, comme le ferait un autre domaine. */
    @Singleton
    static class ContractEvents {

        final List<Object> received = new CopyOnWriteArrayList<>();

        @EventListener
        void on(HouseholdCreatedV1 event) {
            received.add(event);
        }

        @EventListener
        void on(MemberJoinedV1 event) {
            received.add(event);
        }
    }

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    ContractEvents contractEvents;

    @Test
    void creating_and_joining_a_household_are_announced() {
        var api = new HouseholdApi(client.toBlocking());
        var founder = UUID.fromString(KEYCLOAK.activeToken("token-lea", "lea@example.org", "Léa"));
        var guest = UUID.fromString(KEYCLOAK.activeToken("token-max", "max@example.org", "Max"));

        var household = api.create("token-lea", "Chez Léa");
        api.join("token-max", api.issueInvitation("token-lea", household.id()).code());

        assertThat(contractEvents.received)
                .filteredOn(event -> householdOf(event).equals(household.id()))
                .satisfiesExactly(
                        created -> assertThat(((HouseholdCreatedV1) created).founderId()).isEqualTo(founder),
                        joined -> assertThat(((MemberJoinedV1) joined).memberId()).isEqualTo(guest));
    }

    private static UUID householdOf(Object event) {
        return switch (event) {
            case HouseholdCreatedV1 created -> created.householdId();
            case MemberJoinedV1 joined -> joined.householdId();
            default -> throw new IllegalArgumentException("événement inattendu : " + event);
        };
    }
}
