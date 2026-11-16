package io.github.doriangrelu.lifemanager.budget.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;

/** Une autre catégorie du foyer porte déjà ce nom (sans tenir compte des majuscules). */
public class DuplicateCategoryNameException extends ConflictException {

    public DuplicateCategoryNameException() {
        super("Une catégorie porte déjà ce nom");
    }
}
