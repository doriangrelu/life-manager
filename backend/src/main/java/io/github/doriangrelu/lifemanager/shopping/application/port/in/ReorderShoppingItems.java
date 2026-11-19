package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ReorderShoppingItemsCommand;

/** Range les articles d'une sous-liste dans l'ordre voulu par un membre du foyer. */
public interface ReorderShoppingItems {

    void reorder(ReorderShoppingItemsCommand command);
}
