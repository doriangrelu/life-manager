package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.SetCategoryBudgetCommand;

/** Fixe ou retire le budget prévu d'une catégorie de dépenses, à partir d'un mois ou pour ce mois seulement. */
public interface SetCategoryBudget {

    void set(SetCategoryBudgetCommand command);
}
