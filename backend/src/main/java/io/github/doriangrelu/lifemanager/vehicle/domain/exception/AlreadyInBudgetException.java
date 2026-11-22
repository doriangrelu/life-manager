package io.github.doriangrelu.lifemanager.vehicle.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;

/** L'opération est déjà dans le budget : elle ne produit jamais deux dépenses. */
public class AlreadyInBudgetException extends ConflictException {

    public AlreadyInBudgetException() {
        super("Cette opération est déjà dans le budget");
    }
}
