package io.github.doriangrelu.lifemanager.shopping.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Article inexistant dans la liste de ce foyer. */
public class ShoppingItemNotFoundException extends NotFoundException {

    public ShoppingItemNotFoundException() {
        super("Article introuvable");
    }
}
