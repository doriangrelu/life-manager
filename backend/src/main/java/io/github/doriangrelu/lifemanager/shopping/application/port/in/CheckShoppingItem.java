package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ShoppingItemCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;
/** Coche un article (sans effet s'il l'est déjà). */
public interface CheckShoppingItem {

    ShoppingItemView check(ShoppingItemCommand command);
}
