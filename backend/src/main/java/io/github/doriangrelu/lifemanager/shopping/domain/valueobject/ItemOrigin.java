package io.github.doriangrelu.lifemanager.shopping.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Instant;

/** Qui a ajouté un article à la liste, et quand. */
public record ItemOrigin(MemberId addedBy, Instant addedAt) {

    public ItemOrigin {
        Require.notNull(addedBy, "L'auteur de l'ajout");
        Require.notNull(addedAt, "La date d'ajout");
    }
}
