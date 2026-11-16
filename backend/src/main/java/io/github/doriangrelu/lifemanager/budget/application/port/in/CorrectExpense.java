package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CorrectExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ExpenseView;
/** Corrige une dépense existante (tous ses champs, mêmes règles qu'à la saisie). */
public interface CorrectExpense {

    ExpenseView correct(CorrectExpenseCommand command);
}
