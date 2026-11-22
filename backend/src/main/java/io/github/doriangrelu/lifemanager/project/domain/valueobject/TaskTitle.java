package io.github.doriangrelu.lifemanager.project.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Titre d'une tâche (« Choisir le carrelage ») : 1 à 120 caractères. */
public record TaskTitle(String value) {

    private static final String FIELD = "Le titre de la tâche";
    private static final int MAX_LENGTH = 120;

    public TaskTitle {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }
}
