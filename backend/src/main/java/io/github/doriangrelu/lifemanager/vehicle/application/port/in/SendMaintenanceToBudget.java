package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.SendToBudgetCommand;

/** Ajoute au budget le coût d'une opération déjà saisie. */
public interface SendMaintenanceToBudget {

    void send(SendToBudgetCommand command);
}
