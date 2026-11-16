package io.github.doriangrelu.lifemanager.identity.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/**
 * Nom affiché d'un utilisateur : 1 à 100 caractères, sans espaces autour.
 */
public record DisplayName(String value) {

    private static final String FIELD = "Le nom d'affichage";
    private static final int MAX_LENGTH = 100;

    public DisplayName {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }
}
