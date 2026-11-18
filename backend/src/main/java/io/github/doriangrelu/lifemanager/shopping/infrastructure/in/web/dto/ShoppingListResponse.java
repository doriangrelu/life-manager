package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingListView;
import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;

/**
 * Une sous-liste de courses.
 *
 * @param pendingCount nombre d'articles non cochés
 */
@Serdeable
public record ShoppingListResponse(UUID id, String name, long pendingCount) {

    public static ShoppingListResponse from(ShoppingListView list) {
        return new ShoppingListResponse(list.id(), list.name(), list.pendingCount());
    }
}
