package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.text.Names;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Nom d'une charge (« internet », « assurance habitation »…) : 1 à 60 caractères, en minuscules. */
public record ChargeName(String value) {

    private static final String FIELD = "Le nom de la charge";
    private static final int MAX_LENGTH = 60;

    public ChargeName {
        value = Require.maxLength(Names.normalized(Require.notBlank(value, FIELD)), MAX_LENGTH, FIELD);
    }
}
