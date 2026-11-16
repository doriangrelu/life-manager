package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RenameCategoryCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
/** Renomme une catégorie du foyer ; ses dépenses y restent rattachées. */
public interface RenameCategory {

    CategoryView rename(RenameCategoryCommand command);
}
