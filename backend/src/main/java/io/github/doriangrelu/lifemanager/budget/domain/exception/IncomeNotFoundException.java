package io.github.doriangrelu.lifemanager.budget.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Rentrée inconnue, ou d'un autre foyer. */
public class IncomeNotFoundException extends NotFoundException {

    public IncomeNotFoundException() {
        super("Rentrée introuvable");
    }
}
