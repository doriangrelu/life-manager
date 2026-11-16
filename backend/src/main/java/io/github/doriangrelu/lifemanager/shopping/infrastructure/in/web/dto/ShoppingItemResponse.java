package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/** Un article de la liste de courses. */
@Serdeable
public record ShoppingItemResponse(UUID id, String label, boolean checked, Instant addedAt) {

    public static ShoppingItemResponse from(ShoppingItemView item) {
        return new ShoppingItemResponse(item.id(), item.label(), item.checked(), item.addedAt());
    }
}
