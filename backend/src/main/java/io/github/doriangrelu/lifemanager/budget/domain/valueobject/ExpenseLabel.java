package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.text.Names;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Libellé libre d'une dépense (« plein d'essence »…) : 140 caractères au plus, en minuscules. */
public record ExpenseLabel(String value) {

    private static final String FIELD = "Le libellé";
    private static final int MAX_LENGTH = 140;

    public ExpenseLabel {
        value = Require.maxLength(Names.normalized(Require.notBlank(value, FIELD)), MAX_LENGTH, FIELD);
    }

    /** Le libellé est facultatif : un texte absent ou vide donne {@code null} (pas de libellé). */
    public static ExpenseLabel ofNullable(String text) {
        return text == null || text.isBlank() ? null : new ExpenseLabel(text);
    }
}
