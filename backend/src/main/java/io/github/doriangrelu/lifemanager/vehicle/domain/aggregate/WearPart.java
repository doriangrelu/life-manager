package io.github.doriangrelu.lifemanager.vehicle.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Mileage;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartKind;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartRemoval;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Pièce d'usure d'un véhicule (pneus, plaquettes…) : montée tant qu'elle n'a pas été remplacée.
 * Invariants : jamais posée un jour à venir ; une dépose suit toujours la pose (date et kilométrage).
 */
public final class WearPart extends AggregateRoot<WearPartId> {

    private final VehicleId vehicleId;
    private final WearPartKind kind;
    private WearPartDetails details;
    private WearPartRemoval removal;

    private WearPart(WearPartId id, VehicleId vehicleId, WearPartKind kind, WearPartDetails details,
                     WearPartRemoval removal) {
        super(id);
        this.vehicleId = Require.notNull(vehicleId, "Le véhicule");
        this.kind = Require.notNull(kind, "Le type de pièce");
        this.details = Require.notNull(details, "La pose");
        this.removal = removal;
    }

    /** Pose une pièce sur le véhicule. */
    public static WearPart fit(WearPartId id, VehicleId vehicleId, WearPartKind kind, WearPartDetails details,
                               Clock clock) {
        notInFuture(details.fittedOn(), clock);
        return new WearPart(id, vehicleId, kind, details, null);
    }

    /** Recharge depuis le stockage ; {@code removal} vaut {@code null} pour une pièce montée. */
    public static WearPart restore(WearPartId id, VehicleId vehicleId, WearPartKind kind, WearPartDetails details,
                                   WearPartRemoval removal) {
        return new WearPart(id, vehicleId, kind, details, removal);
    }

    /** Corrige la pose (erreur de saisie) ; une pièce déposée garde sa dépose, qui doit rester après la pose. */
    public void correct(WearPartDetails corrected, Clock clock) {
        notInFuture(corrected.fittedOn(), clock);
        if (removal != null) {
            requireAfter(corrected, removal.removedOn(), removal.removedKm());
        }
        details = corrected;
    }

    /** Dépose la pièce, remplacée par {@code next} (posée le même jour, au même kilométrage). */
    public void replaceWith(WearPartDetails next) {
        requireAfter(details, next.fittedOn(), next.fittedKm());
        removal = new WearPartRemoval(next.fittedOn(), next.fittedKm());
    }

    /** Km parcourus : jusqu'à la dépose, sinon jusqu'au kilométrage actuel du véhicule (vide s'il est inconnu). */
    public Optional<Integer> kmDone(Mileage current) {
        var end = removal != null ? removal.removedKm() : current;
        return Optional.ofNullable(end).map(km -> Math.max(0, km.kilometers() - details.fittedKm().kilometers()));
    }

    private static void requireAfter(WearPartDetails fitted, LocalDate on, Mileage km) {
        if (on.isBefore(fitted.fittedOn()) || km.kilometers() < fitted.fittedKm().kilometers()) {
            throw new InvalidValueException("La nouvelle pièce doit être posée après l'ancienne (date et kilométrage)");
        }
    }

    private static void notInFuture(LocalDate day, Clock clock) {
        if (day.isAfter(LocalDate.now(clock))) {
            throw new InvalidValueException("La date de pose ne peut pas être dans le futur");
        }
    }

    public boolean isMounted() {
        return removal == null;
    }

    public boolean belongsTo(VehicleId vehicle) {
        return vehicleId.equals(vehicle);
    }

    public VehicleId vehicleId() {
        return vehicleId;
    }

    public WearPartKind kind() {
        return kind;
    }

    public WearPartDetails details() {
        return details;
    }

    public Optional<WearPartRemoval> removal() {
        return Optional.ofNullable(removal);
    }
}
