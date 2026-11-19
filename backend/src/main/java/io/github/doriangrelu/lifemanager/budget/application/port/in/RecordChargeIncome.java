package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordChargeExpenseCommand;

/**
 * Enregistre la rentrée d'une échéance de rentrée récurrente arrivée (salaire, aide…).
 * Même commande que pour une dépense : le « payeur » y est le membre qui reçoit.
 */
public interface RecordChargeIncome {

    void record(RecordChargeExpenseCommand command);
}
