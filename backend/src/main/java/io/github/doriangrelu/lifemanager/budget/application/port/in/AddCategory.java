package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.AddCategoryCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
/** Ajoute une catégorie au foyer. */
public interface AddCategory {

    CategoryView add(AddCategoryCommand command);
}
