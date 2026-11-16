package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.query.ListShoppingItemsQuery;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;

import java.util.List;
/** Liste de courses d'un foyer : non cochés du plus ancien au plus récent, puis cochés du dernier au premier. */
public interface ListShoppingItems {

    List<ShoppingItemView> list(ListShoppingItemsQuery query);
}
