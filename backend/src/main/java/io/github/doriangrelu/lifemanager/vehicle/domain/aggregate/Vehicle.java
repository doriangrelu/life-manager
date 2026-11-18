package io.github.doriangrelu.lifemanager.vehicle.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleRegistered;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleRemoved;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleUpdated;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.NextDeadline;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;

import java.time.Clock;

/**
 * Fiche d'un véhicule du foyer, avec ses prochaines échéances (contrôle technique, révision).
 * Invariants : un nom valide, un seul foyer. Chaque changement est annoncé par un événement (futurs rappels).
 */
public final class Vehicle extends AggregateRoot<VehicleId> {

    private final HouseholdId householdId;
    private VehicleDetails details;

    private Vehicle(VehicleId id, HouseholdId householdId, VehicleDetails details) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer du véhicule");
        this.details = Require.notNull(details, "La fiche du véhicule");
    }

    /** Ajoute un véhicule au foyer. */
    public static Vehicle register(VehicleId id, HouseholdId householdId, VehicleDetails details, Clock clock) {
        var vehicle = new Vehicle(id, householdId, details);
        vehicle.recordEvent(new VehicleRegistered(id, householdId, details, clock.instant()));
        return vehicle;
    }

    /** Recharge un véhicule depuis le stockage (aucun événement produit). */
    public static Vehicle restore(VehicleId id, HouseholdId householdId, VehicleDetails details) {
        return new Vehicle(id, householdId, details);
    }

    /** Remplace la fiche (nom, immatriculation, échéances). */
    public void update(VehicleDetails newDetails, Clock clock) {
        details = Require.notNull(newDetails, "La fiche du véhicule");
        recordEvent(new VehicleUpdated(id(), householdId, details, clock.instant()));
    }

    /** Une opération du carnet (contrôle technique, révision) fixe la prochaine échéance de son type. */
    public void postpone(NextDeadline next, Clock clock) {
        update(new VehicleDetails(details.name(), details.licensePlate(), details.deadlines().with(next)), clock);
    }

    /** À appeler juste avant de supprimer le véhicule, pour annoncer sa suppression. */
    public void markRemoved(Clock clock) {
        recordEvent(new VehicleRemoved(id(), householdId, clock.instant()));
    }

    public boolean belongsTo(HouseholdId household) {
        return householdId.equals(household);
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public VehicleDetails details() {
        return details;
    }
}
