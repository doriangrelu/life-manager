package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ShoppingListCommand;

/** Supprime une sous-liste avec ses articles et son historique. */
public interface DeleteShoppingList {

    void delete(ShoppingListCommand command);
}
