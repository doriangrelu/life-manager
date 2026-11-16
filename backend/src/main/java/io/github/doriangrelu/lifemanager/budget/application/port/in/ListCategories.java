package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.query.ListCategoriesQuery;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;

import java.util.List;
/** Liste les catégories d'un foyer, archivées comprises. */
public interface ListCategories {

    List<CategoryView> list(ListCategoriesQuery query);
}
