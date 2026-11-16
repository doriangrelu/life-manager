package io.github.doriangrelu.lifemanager.budget.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Catégorie inexistante dans ce foyer. */
public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException() {
        super("Catégorie introuvable");
    }
}
