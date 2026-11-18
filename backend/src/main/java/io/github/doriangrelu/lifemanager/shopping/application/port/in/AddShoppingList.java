package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.AddShoppingListCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingListView;

/** Ajoute une sous-liste vide au foyer. */
public interface AddShoppingList {

    ShoppingListView add(AddShoppingListCommand command);
}
