package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ClearCheckedItemsCommand;
/** Supprime tous les articles cochés de la liste du foyer. */
public interface ClearCheckedItems {

    void clear(ClearCheckedItemsCommand command);
}
