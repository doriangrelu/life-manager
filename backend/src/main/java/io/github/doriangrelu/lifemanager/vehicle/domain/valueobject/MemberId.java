package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'un membre de foyer : le {@code sub} Keycloak de l'utilisateur. */
public record MemberId(UUID value) {

    private static final String FIELD = "L'identifiant du membre";

    public MemberId {
        Require.notNull(value, FIELD);
    }

    /** Construit l'identifiant depuis le {@code sub} Keycloak reçu sous forme de texte. */
    public static MemberId of(String subject) {
        try {
            return new MemberId(UUID.fromString(Require.notBlank(subject, FIELD)));
        } catch (IllegalArgumentException notAUuid) {
            throw new InvalidValueException(FIELD + " doit être un UUID");
        }
    }
}
