package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.UpdateVehicleCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.VehicleView;
/** Modifie la fiche d'un véhicule (nom, immatriculation, échéances). */
public interface UpdateVehicle {

    VehicleView update(UpdateVehicleCommand command);
}
