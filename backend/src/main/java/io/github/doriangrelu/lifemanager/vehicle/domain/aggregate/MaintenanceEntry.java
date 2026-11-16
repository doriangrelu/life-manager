package io.github.doriangrelu.lifemanager.vehicle.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;
import io.github.doriangrelu.lifemanager.vehicle.domain.exception.FutureMaintenanceDateException;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Opération du carnet d'entretien d'un véhicule (révision, vidange…).
 * Invariant : jamais datée d'un jour à venir ; les bornes des valeurs sont garanties par {@link MaintenanceDetails}.
 */
public final class MaintenanceEntry extends AggregateRoot<MaintenanceEntryId> {

    private final VehicleId vehicleId;
    private final MaintenanceDetails details;
    private final MemberId recordedBy;
    private final Instant recordedAt;

    private MaintenanceEntry(MaintenanceEntryId id, VehicleId vehicleId, MaintenanceDetails details,
                             MemberId recordedBy, Instant recordedAt) {
        super(id);
        this.vehicleId = Require.notNull(vehicleId, "Le véhicule");
        this.details = Require.notNull(details, "Le détail de l'opération");
        this.recordedBy = Require.notNull(recordedBy, "L'auteur de la saisie");
        this.recordedAt = Require.notNull(recordedAt, "La date de saisie");
    }

    /** Ajoute une opération au carnet ; {@code clock} donne le jour courant pour refuser une date future. */
    public static MaintenanceEntry record(MaintenanceEntryId id, VehicleId vehicleId, MaintenanceDetails details,
                                         MemberId recordedBy, Clock clock) {
        if (details.performedOn().isAfter(LocalDate.now(clock))) {
            throw new FutureMaintenanceDateException();
        }
        return new MaintenanceEntry(id, vehicleId, details, recordedBy, clock.instant());
    }

    /** Recharge une opération depuis le stockage. */
    public static MaintenanceEntry restore(MaintenanceEntryId id, VehicleId vehicleId, MaintenanceDetails details,
                                           MemberId recordedBy, Instant recordedAt) {
        return new MaintenanceEntry(id, vehicleId, details, recordedBy, recordedAt);
    }

    public boolean belongsTo(VehicleId vehicle) {
        return vehicleId.equals(vehicle);
    }

    public VehicleId vehicleId() {
        return vehicleId;
    }

    public MaintenanceDetails details() {
        return details;
    }

    public MemberId recordedBy() {
        return recordedBy;
    }

    public Instant recordedAt() {
        return recordedAt;
    }
}
