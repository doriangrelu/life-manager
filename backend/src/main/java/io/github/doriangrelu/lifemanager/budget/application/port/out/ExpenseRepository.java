package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Expense;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;

import java.util.Optional;
/** Stockage des dépenses. */
public interface ExpenseRepository {

    Optional<Expense> findById(ExpenseId id);

    /** Crée la dépense ou enregistre ses modifications. */
    void save(Expense expense);

    /** Supprime définitivement la dépense. */
    void delete(Expense expense);
}
