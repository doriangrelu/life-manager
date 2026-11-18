package io.github.doriangrelu.lifemanager.budget.domain.aggregate;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    private static final HouseholdId HOME = new HouseholdId(UUID.randomUUID());
    private static final HouseholdId OTHER = new HouseholdId(UUID.randomUUID());

    @Test
    void a_new_household_gets_eight_active_default_categories() {
        var defaults = Category.defaultsFor(HOME);

        assertThat(defaults).extracting(category -> category.name().value()).containsExactly(
                "courses", "logement", "transport", "santé", "loisirs", "restaurants", "abonnements", "autre");
        assertThat(defaults).allMatch(category -> category.isUsableBy(HOME));
    }

    @Test
    void an_archived_category_is_no_longer_usable_until_reactivated() {
        var category = Category.create(CategoryId.newId(), HOME, new CategoryName("Abonnements"));

        category.archive();
        assertThat(category.isUsableBy(HOME)).isFalse();

        category.reactivate();
        assertThat(category.isUsableBy(HOME)).isTrue();
    }

    @Test
    void a_category_is_only_usable_by_its_household() {
        var category = Category.create(CategoryId.newId(), HOME, new CategoryName("Courses"));

        assertThat(category.isUsableBy(OTHER)).isFalse();
    }

    @Test
    void renaming_keeps_the_identity() {
        var category = Category.create(CategoryId.newId(), HOME, new CategoryName("Loisirs"));
        var id = category.id();

        category.rename(new CategoryName("Sorties"));

        assertThat(category.name()).isEqualTo(new CategoryName("sorties"));
        assertThat(category.id()).isEqualTo(id);
    }
}
