package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.RenameShoppingListCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingListView;

/** Renomme une sous-liste du foyer. */
public interface RenameShoppingList {

    ShoppingListView rename(RenameShoppingListCommand command);
}
