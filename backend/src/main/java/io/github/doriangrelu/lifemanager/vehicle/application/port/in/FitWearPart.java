package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.WearPartView;

/** Pose une pièce ; celle du même type déjà montée passe dans l'historique. */
public interface FitWearPart {

    WearPartView fit(WearPartCommand command);
}
