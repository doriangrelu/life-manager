package io.github.doriangrelu.lifemanager.budget.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Dépense inexistante dans ce foyer. */
public class ExpenseNotFoundException extends NotFoundException {

    public ExpenseNotFoundException() {
        super("Dépense introuvable");
    }
}
