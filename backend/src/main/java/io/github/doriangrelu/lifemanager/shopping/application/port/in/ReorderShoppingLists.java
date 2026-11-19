package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ReorderShoppingListsCommand;

/** Range les sous-listes du foyer dans l'ordre voulu par un de ses membres. */
public interface ReorderShoppingLists {

    void reorder(ReorderShoppingListsCommand command);
}
