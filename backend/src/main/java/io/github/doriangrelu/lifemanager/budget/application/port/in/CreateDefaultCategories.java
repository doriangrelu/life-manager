package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CreateDefaultCategoriesCommand;
/** Donne à un nouveau foyer ses catégories par défaut (une seule fois). */
public interface CreateDefaultCategories {

    void create(CreateDefaultCategoriesCommand command);
}
