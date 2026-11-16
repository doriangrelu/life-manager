package io.github.doriangrelu.lifemanager.shopping.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Foyer inconnu du domaine courses, ou dont l'utilisateur n'est pas membre (même erreur dans les deux cas). */
public class ShoppingHouseholdNotFoundException extends NotFoundException {

    public ShoppingHouseholdNotFoundException() {
        super("Foyer introuvable");
    }
}
