package io.github.doriangrelu.lifemanager.household.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'une invitation. */
public record InvitationId(UUID value) {

    public InvitationId {
        Require.notNull(value, "L'identifiant de l'invitation");
    }

    public static InvitationId newId() {
        return new InvitationId(UUID.randomUUID());
    }
}
