package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ExpenseView;
/** Enregistre une dépense dans un foyer. */
public interface RecordExpense {

    ExpenseView record(RecordExpenseCommand command);
}
