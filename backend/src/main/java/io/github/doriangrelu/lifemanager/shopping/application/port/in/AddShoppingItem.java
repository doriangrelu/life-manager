package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.AddShoppingItemCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;
/** Ajoute un article non coché à la liste du foyer. */
public interface AddShoppingItem {

    ShoppingItemView add(AddShoppingItemCommand command);
}
