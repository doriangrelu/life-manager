package io.github.doriangrelu.lifemanager.budget.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Une rentrée ne peut pas être datée d'un jour à venir (une rentrée prévue est une rentrée récurrente). */
public class FutureIncomeDateException extends InvalidValueException {

    public FutureIncomeDateException() {
        super("La date d'une rentrée ne peut pas être dans le futur");
    }
}
