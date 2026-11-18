package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.CreateDefaultShoppingListsCommand;

/** Donne ses sous-listes par défaut à un nouveau foyer (sans effet s'il en a déjà). */
public interface CreateDefaultShoppingLists {

    void create(CreateDefaultShoppingListsCommand command);
}
