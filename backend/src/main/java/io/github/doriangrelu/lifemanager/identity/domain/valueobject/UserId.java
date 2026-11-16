package io.github.doriangrelu.lifemanager.identity.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/**
 * Identifiant d'un utilisateur : le {@code sub} attribué par Keycloak (un UUID).
 * C'est le même identifiant dans tous les domaines.
 */
public record UserId(UUID value) {

    private static final String FIELD = "L'identifiant utilisateur";

    public UserId {
        Require.notNull(value, FIELD);
    }

    /** Construit l'identifiant depuis le {@code sub} Keycloak reçu sous forme de texte. */
    public static UserId of(String subject) {
        try {
            return new UserId(UUID.fromString(Require.notBlank(subject, FIELD)));
        } catch (IllegalArgumentException notAUuid) {
            throw new InvalidValueException(FIELD + " doit être un UUID");
        }
    }
}
