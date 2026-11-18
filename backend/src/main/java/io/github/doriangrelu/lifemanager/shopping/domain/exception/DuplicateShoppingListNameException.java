package io.github.doriangrelu.lifemanager.shopping.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;

/** Une autre sous-liste du foyer porte déjà ce nom (sans tenir compte des majuscules). */
public class DuplicateShoppingListNameException extends ConflictException {

    public DuplicateShoppingListNameException() {
        super("Une sous-liste porte déjà ce nom");
    }
}
