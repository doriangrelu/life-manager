package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ShoppingItemCommand;
/** Supprime un article de la liste. */
public interface RemoveShoppingItem {

    void remove(ShoppingItemCommand command);
}
