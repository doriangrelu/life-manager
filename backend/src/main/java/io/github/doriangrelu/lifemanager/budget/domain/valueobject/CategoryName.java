package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Locale;

/** Nom d'une catégorie de dépenses (« Courses », « Transport »…) : 1 à 40 caractères. */
public record CategoryName(String value) {

    private static final String FIELD = "Le nom de la catégorie";
    private static final int MAX_LENGTH = 40;

    public CategoryName {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }

    /** Vrai si les deux noms ne diffèrent que par les majuscules (« Courses » et « courses »). */
    public boolean sameAs(CategoryName other) {
        return value.toLowerCase(Locale.ROOT).equals(other.value.toLowerCase(Locale.ROOT));
    }
}
