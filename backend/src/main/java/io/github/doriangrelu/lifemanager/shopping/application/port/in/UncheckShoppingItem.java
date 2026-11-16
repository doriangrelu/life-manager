package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ShoppingItemCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;
/** Décoche un article (sans effet s'il ne l'est pas). */
public interface UncheckShoppingItem {

    ShoppingItemView uncheck(ShoppingItemCommand command);
}
