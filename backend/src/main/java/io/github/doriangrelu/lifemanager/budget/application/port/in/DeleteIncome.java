package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.DeleteIncomeCommand;

/** Supprime une rentrée d'argent. */
public interface DeleteIncome {

    void delete(DeleteIncomeCommand command);
}
