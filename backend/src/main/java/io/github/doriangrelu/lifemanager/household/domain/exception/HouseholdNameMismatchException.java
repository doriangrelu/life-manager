package io.github.doriangrelu.lifemanager.household.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Suppression demandée sans retaper exactement le nom du foyer. */
public class HouseholdNameMismatchException extends InvalidValueException {

    public HouseholdNameMismatchException() {
        super("Le nom saisi ne correspond pas au nom du foyer");
    }
}
