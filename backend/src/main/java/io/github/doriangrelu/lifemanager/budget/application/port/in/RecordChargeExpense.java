package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordChargeExpenseCommand;

/** Enregistre la dépense d'une échéance de charge payée (une seule par échéance). */
public interface RecordChargeExpense {

    void record(RecordChargeExpenseCommand command);
}
