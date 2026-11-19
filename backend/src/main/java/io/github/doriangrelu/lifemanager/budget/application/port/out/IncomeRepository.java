package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Income;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeId;

import java.util.Optional;

/** Stockage des rentrées d'argent. */
public interface IncomeRepository {

    Optional<Income> findById(IncomeId id);

    /** Crée la rentrée ou enregistre ses modifications. */
    void save(Income income);

    /** Supprime définitivement la rentrée. */
    void delete(Income income);
}
