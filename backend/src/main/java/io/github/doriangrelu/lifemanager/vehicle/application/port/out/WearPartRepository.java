package io.github.doriangrelu.lifemanager.vehicle.application.port.out;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.WearPart;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartKind;

import java.util.List;
import java.util.Optional;

/** Stockage des pièces d'usure. */
public interface WearPartRepository {

    Optional<WearPart> findById(WearPartId id);

    /** Toutes les pièces du véhicule, montées et déposées. */
    List<WearPart> findAllByVehicle(VehicleId vehicleId);

    /** Pièce de ce type actuellement montée sur le véhicule. */
    Optional<WearPart> findMounted(VehicleId vehicleId, WearPartKind kind);

    /** Crée la pièce ou enregistre ses modifications. */
    void save(WearPart part);

    void delete(WearPart part);
}
