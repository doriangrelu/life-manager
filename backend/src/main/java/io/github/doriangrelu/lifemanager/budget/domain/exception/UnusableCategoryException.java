package io.github.doriangrelu.lifemanager.budget.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** La catégorie choisie pour une dépense est archivée ou n'appartient pas au foyer. */
public class UnusableCategoryException extends InvalidValueException {

    public UnusableCategoryException() {
        super("Cette catégorie ne peut pas être utilisée pour une dépense");
    }
}
