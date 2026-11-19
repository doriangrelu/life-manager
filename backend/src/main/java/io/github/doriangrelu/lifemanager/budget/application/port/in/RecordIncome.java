package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordIncomeCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.IncomeView;

/** Enregistre une rentrée d'argent dans un foyer. */
public interface RecordIncome {

    IncomeView record(RecordIncomeCommand command);
}
