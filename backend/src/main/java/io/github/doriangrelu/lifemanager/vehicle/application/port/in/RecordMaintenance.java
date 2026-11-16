package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RecordMaintenanceCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.MaintenanceEntryView;
/** Ajoute une opération au carnet d'un véhicule. */
public interface RecordMaintenance {

    MaintenanceEntryView record(RecordMaintenanceCommand command);
}
