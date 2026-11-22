package io.github.doriangrelu.lifemanager.project.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Nom d'un projet (« Salle de bain », « Vacances 2027 ») : 1 à 60 caractères. */
public record ProjectName(String value) {

    private static final String FIELD = "Le nom du projet";
    private static final int MAX_LENGTH = 60;

    public ProjectName {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }
}
