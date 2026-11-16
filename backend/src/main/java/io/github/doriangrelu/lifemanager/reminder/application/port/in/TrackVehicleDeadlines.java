package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.TrackVehicleDeadlinesCommand;
/** Remplace les échéances suivies d'un véhicule par son état actuel. */
public interface TrackVehicleDeadlines {

    void track(TrackVehicleDeadlinesCommand command);
}
