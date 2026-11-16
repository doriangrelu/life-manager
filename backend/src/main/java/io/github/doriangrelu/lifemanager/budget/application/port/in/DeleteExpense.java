package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.DeleteExpenseCommand;
/** Supprime définitivement une dépense. */
public interface DeleteExpense {

    void delete(DeleteExpenseCommand command);
}
