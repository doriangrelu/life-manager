package io.github.doriangrelu.lifemanager.household.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.household.application.port.in.ListMemberships;
import io.github.doriangrelu.lifemanager.household.contract.event.MembershipKnownV1;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;

import java.time.Clock;

/**
 * Au démarrage, rappelle aux autres domaines qui est membre de quel foyer.
 * Rattrape les foyers créés avant l'arrivée d'un domaine (ou un événement perdu).
 */
@Singleton
class MembershipAnnouncer {

    private final ListMemberships listMemberships;
    private final ApplicationEventPublisher<Object> eventBus;
    private final Clock clock;

    MembershipAnnouncer(ListMemberships listMemberships, ApplicationEventPublisher<Object> eventBus, Clock clock) {
        this.listMemberships = listMemberships;
        this.eventBus = eventBus;
        this.clock = clock;
    }

    @EventListener
    void on(ServerStartupEvent event) {
        var now = clock.instant();
        listMemberships.listAll().forEach(membership -> eventBus.publishEvent(
                new MembershipKnownV1(membership.householdId(), membership.memberId(), now)));
    }
}
