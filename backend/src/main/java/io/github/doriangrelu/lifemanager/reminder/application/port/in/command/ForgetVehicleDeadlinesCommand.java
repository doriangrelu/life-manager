package io.github.doriangrelu.lifemanager.reminder.application.port.in.command;

import java.util.UUID;
/** Véhicule supprimé : ses échéances ne sont plus suivies. */
public record ForgetVehicleDeadlinesCommand(UUID vehicleId) {
}
