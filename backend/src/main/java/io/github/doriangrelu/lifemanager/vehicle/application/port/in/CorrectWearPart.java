package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.WearPartView;

/** Corrige la pose d'une pièce (erreur de saisie). */
public interface CorrectWearPart {

    WearPartView correct(WearPartCommand command);
}
