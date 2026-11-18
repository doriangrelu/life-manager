package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.query.ListShoppingListsQuery;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingListView;

import java.util.List;

/** Sous-listes du foyer, triées par nom, avec leur nombre d'articles non cochés. */
public interface ListShoppingLists {

    List<ShoppingListView> list(ListShoppingListsQuery query);
}
