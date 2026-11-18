package io.github.doriangrelu.lifemanager.shopping.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.text.Names;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Libellé d'un article de courses (« 2 l de lait ») : 1 à 60 caractères, en minuscules. */
public record ItemLabel(String value) {

    private static final String FIELD = "Le libellé de l'article";
    private static final int MAX_LENGTH = 60;

    public ItemLabel {
        value = Require.maxLength(Names.normalized(Require.notBlank(value, FIELD)), MAX_LENGTH, FIELD);
    }
}
