package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.WearPartView;

import java.util.List;

/** Liste les pièces d'usure du véhicule (montées et historique). */
public interface ListWearParts {

    List<WearPartView> list(WearPartCommand command);
}
