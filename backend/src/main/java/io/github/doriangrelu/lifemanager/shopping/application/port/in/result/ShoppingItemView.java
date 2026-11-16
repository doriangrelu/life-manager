package io.github.doriangrelu.lifemanager.shopping.application.port.in.result;

import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;

import java.time.Instant;
import java.util.UUID;
/** Un article de courses tel que vu hors du domaine. */
public record ShoppingItemView(UUID id, String label, boolean checked, Instant addedAt) {

    public static ShoppingItemView from(ShoppingItem item) {
        return new ShoppingItemView(item.id().value(), item.label().value(), item.isChecked(),
                item.origin().addedAt());
    }
}
