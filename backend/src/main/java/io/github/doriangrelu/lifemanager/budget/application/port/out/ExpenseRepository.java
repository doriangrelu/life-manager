package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Expense;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectId;

import java.util.List;
import java.util.Optional;
/** Stockage des dépenses. */
public interface ExpenseRepository {

    Optional<Expense> findById(ExpenseId id);

    /** Dépenses d'un projet du foyer, toutes dates, la plus récente en premier. */
    List<Expense> findForProject(HouseholdId householdId, ProjectId projectId);

    /** Crée la dépense ou enregistre ses modifications. */
    void save(Expense expense);

    /** Supprime définitivement la dépense. */
    void delete(Expense expense);
}
