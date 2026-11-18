package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox.Outbox;
import io.github.doriangrelu.lifemanager.vehicle.contract.event.VehicleDeadlinesChangedV1;
import io.github.doriangrelu.lifemanager.vehicle.contract.event.VehicleRemovedV1;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleRegistered;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleRemoved;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleUpdated;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.time.Instant;

/**
 * Traduit les événements internes du véhicule en événements de contrat pour les autres domaines.
 * Ils sont écrits dans la boîte d'envoi avec le changement (même transaction), puis distribués.
 */
@Singleton
class VehicleContractEventRelay {

    private final Outbox outbox;

    VehicleContractEventRelay(@Named("vehicle") Outbox outbox) {
        this.outbox = outbox;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "vehicle")
    void on(VehicleRegistered event) {
        recordDeadlines(event.vehicleId(), event.householdId(), event.details(), event.occurredAt());
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "vehicle")
    void on(VehicleUpdated event) {
        recordDeadlines(event.vehicleId(), event.householdId(), event.details(), event.occurredAt());
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "vehicle")
    void on(VehicleRemoved event) {
        outbox.record(new VehicleRemovedV1(event.vehicleId().value(), event.householdId().value(),
                event.occurredAt()));
    }

    private void recordDeadlines(VehicleId vehicle, HouseholdId household, VehicleDetails details, Instant at) {
        outbox.record(new VehicleDeadlinesChangedV1(vehicle.value(), household.value(),
                details.name().value(), details.deadlines().technicalInspectionDue(),
                details.deadlines().serviceDue(), at));
    }
}
