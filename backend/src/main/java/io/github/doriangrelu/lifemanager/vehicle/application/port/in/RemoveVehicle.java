package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RemoveVehicleCommand;
/** Supprime définitivement un véhicule et son carnet. */
public interface RemoveVehicle {

    void remove(RemoveVehicleCommand command);
}
