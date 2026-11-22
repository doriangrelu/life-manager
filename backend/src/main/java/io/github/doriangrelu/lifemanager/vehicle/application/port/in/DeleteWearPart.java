package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartCommand;

/** Supprime une pièce d'usure (erreur de saisie). */
public interface DeleteWearPart {

    void delete(WearPartCommand command);
}
