package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.DeleteMaintenanceEntryCommand;
/** Supprime définitivement une opération du carnet. */
public interface DeleteMaintenanceEntry {

    void delete(DeleteMaintenanceEntryCommand command);
}
