package io.github.doriangrelu.lifemanager.shopping.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Sous-liste inexistante dans ce foyer. */
public class ShoppingListNotFoundException extends NotFoundException {

    public ShoppingListNotFoundException() {
        super("Sous-liste introuvable");
    }
}
