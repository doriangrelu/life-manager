package io.github.doriangrelu.lifemanager.shopping.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Nom d'une sous-liste de courses (« Courses », « Bricolage »…) : 1 à 30 caractères. */
public record ListName(String value) {

    private static final String FIELD = "Le nom de la sous-liste";
    private static final int MAX_LENGTH = 30;

    public ListName {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }
}
