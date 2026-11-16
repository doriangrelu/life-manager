package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.ForgetVehicleDeadlinesCommand;
/** Ne suit plus les échéances d'un véhicule supprimé. */
public interface ForgetVehicleDeadlines {

    void forget(ForgetVehicleDeadlinesCommand command);
}
