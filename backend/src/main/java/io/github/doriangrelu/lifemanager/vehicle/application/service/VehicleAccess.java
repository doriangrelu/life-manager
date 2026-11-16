package io.github.doriangrelu.lifemanager.vehicle.application.service;

import io.github.doriangrelu.lifemanager.vehicle.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.VehicleRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.Vehicle;
import io.github.doriangrelu.lifemanager.vehicle.domain.exception.VehicleHouseholdNotFoundException;
import io.github.doriangrelu.lifemanager.vehicle.domain.exception.VehicleNotFoundException;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import jakarta.inject.Singleton;

import java.util.UUID;

/**
 * Cloisonnement du domaine véhicule : seuls les membres d'un foyer accèdent à ses véhicules et à leur carnet.
 * Tout use case qui cible un foyer passe par ici.
 */
@Singleton
public class VehicleAccess {

    private final HouseholdMembers members;
    private final VehicleRepository vehicles;

    VehicleAccess(HouseholdMembers members, VehicleRepository vehicles) {
        this.members = members;
        this.vehicles = vehicles;
    }

    /**
     * Vérifie que le demandeur est membre du foyer et retourne l'identifiant du foyer.
     *
     * @throws VehicleHouseholdNotFoundException sinon (le foyer reste invisible pour un étranger)
     */
    public HouseholdId householdOfMember(UUID householdId, String requesterId) {
        var household = new HouseholdId(householdId);
        if (!members.isMember(household, MemberId.of(requesterId))) {
            throw new VehicleHouseholdNotFoundException();
        }
        return household;
    }

    /** @throws VehicleNotFoundException si le véhicule n'existe pas dans ce foyer */
    public Vehicle loadForMember(UUID householdId, UUID vehicleId, String requesterId) {
        var household = householdOfMember(householdId, requesterId);
        return vehicles.findById(new VehicleId(vehicleId))
                .filter(vehicle -> vehicle.belongsTo(household))
                .orElseThrow(VehicleNotFoundException::new);
    }
}
