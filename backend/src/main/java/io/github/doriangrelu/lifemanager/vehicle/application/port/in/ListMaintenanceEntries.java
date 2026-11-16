package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.query.ListMaintenanceEntriesQuery;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.MaintenanceEntryView;

import java.util.List;
/** Carnet d'entretien d'un véhicule, l'opération la plus récente en premier. */
public interface ListMaintenanceEntries {

    List<MaintenanceEntryView> list(ListMaintenanceEntriesQuery query);
}
