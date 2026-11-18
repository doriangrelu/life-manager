package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.RestockFromHistoryCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;

import java.util.List;

/** Remet en liste, non cochés, des articles choisis dans l'historique (tout ou rien). */
public interface RestockFromHistory {

    /** @return les articles ajoutés */
    List<ShoppingItemView> restock(RestockFromHistoryCommand command);
}
