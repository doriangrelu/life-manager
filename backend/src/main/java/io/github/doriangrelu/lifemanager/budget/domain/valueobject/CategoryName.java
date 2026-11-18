package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.text.Names;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Nom d'une catégorie de dépenses (« courses », « transport »…) : 1 à 40 caractères, en minuscules. */
public record CategoryName(String value) {

    private static final String FIELD = "Le nom de la catégorie";
    private static final int MAX_LENGTH = 40;

    public CategoryName {
        value = Require.maxLength(Names.normalized(Require.notBlank(value, FIELD)), MAX_LENGTH, FIELD);
    }

    /** Vrai si les deux noms désignent la même catégorie (« Courses » et « courses »). */
    public boolean sameAs(CategoryName other) {
        return value.equals(other.value);
    }
}
