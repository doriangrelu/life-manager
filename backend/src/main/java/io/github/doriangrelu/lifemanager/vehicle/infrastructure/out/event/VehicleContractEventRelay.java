package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.vehicle.contract.event.VehicleDeadlinesChangedV1;
import io.github.doriangrelu.lifemanager.vehicle.contract.event.VehicleRemovedV1;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleRegistered;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleRemoved;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleUpdated;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Singleton;

import java.time.Instant;

/**
 * Traduit les événements internes du véhicule en événements de contrat pour les autres domaines,
 * une fois la transaction validée. Point de branchement d'un futur broker externe.
 */
@Singleton
class VehicleContractEventRelay {

    private final ApplicationEventPublisher<Object> eventBus;

    VehicleContractEventRelay(ApplicationEventPublisher<Object> eventBus) {
        this.eventBus = eventBus;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT,
            transactionManager = "vehicle")
    void on(VehicleRegistered event) {
        publishDeadlines(event.vehicleId(), event.householdId(), event.details(), event.occurredAt());
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT,
            transactionManager = "vehicle")
    void on(VehicleUpdated event) {
        publishDeadlines(event.vehicleId(), event.householdId(), event.details(), event.occurredAt());
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT,
            transactionManager = "vehicle")
    void on(VehicleRemoved event) {
        eventBus.publishEvent(new VehicleRemovedV1(event.vehicleId().value(), event.householdId().value(),
                event.occurredAt()));
    }

    private void publishDeadlines(VehicleId vehicle, HouseholdId household, VehicleDetails details, Instant at) {
        eventBus.publishEvent(new VehicleDeadlinesChangedV1(vehicle.value(), household.value(),
                details.name().value(), details.deadlines().technicalInspectionDue(),
                details.deadlines().serviceDue(), at));
    }
}
