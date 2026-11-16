package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RegisterVehicleCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.VehicleView;
/** Ajoute un véhicule au foyer. */
public interface RegisterVehicle {

    VehicleView register(RegisterVehicleCommand command);
}
