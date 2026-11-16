package io.github.doriangrelu.lifemanager.budget.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Une dépense ne peut pas être datée d'un jour à venir. */
public class FutureExpenseDateException extends InvalidValueException {

    public FutureExpenseDateException() {
        super("La date d'une dépense ne peut pas être dans le futur");
    }
}
