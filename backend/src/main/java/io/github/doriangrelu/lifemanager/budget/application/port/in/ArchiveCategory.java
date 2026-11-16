package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CategoryActionCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
/** Archive une catégorie : elle n'est plus proposée pour les nouvelles dépenses. */
public interface ArchiveCategory {

    CategoryView archive(CategoryActionCommand command);
}
