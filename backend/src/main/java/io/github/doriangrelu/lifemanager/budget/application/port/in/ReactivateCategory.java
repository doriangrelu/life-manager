package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CategoryActionCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
/** Réactive une catégorie archivée. */
public interface ReactivateCategory {

    CategoryView reactivate(CategoryActionCommand command);
}
