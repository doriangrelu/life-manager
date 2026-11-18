package io.github.doriangrelu.lifemanager.shopping.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ListName;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShoppingListTest {

    private static final HouseholdId HOUSEHOLD = new HouseholdId(UUID.randomUUID());

    @Test
    void a_name_has_1_to_30_characters() {
        assertThat(new ListName(" Bricolage ").value()).isEqualTo("bricolage");
        assertThatThrownBy(() -> new ListName(" ")).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new ListName("a".repeat(31))).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void a_new_household_gets_three_lists() {
        var lists = ShoppingList.defaultsFor(HOUSEHOLD);

        assertThat(lists).extracting(list -> list.name().value())
                .containsExactly("courses", "bricolage", "vêtements");
        assertThat(lists).allMatch(list -> list.belongsTo(HOUSEHOLD));
        assertThat(lists).extracting(ShoppingList::id).doesNotHaveDuplicates();
    }

    @Test
    void a_list_can_be_renamed() {
        var list = ShoppingList.create(ShoppingListId.newId(), HOUSEHOLD, new ListName("Pharma"));

        list.rename(new ListName("Pharmacie"));

        assertThat(list.name().value()).isEqualTo("pharmacie");
    }

    @Test
    void a_list_belongs_to_one_household() {
        var list = ShoppingList.create(ShoppingListId.newId(), HOUSEHOLD, new ListName("Courses"));

        assertThat(list.belongsTo(new HouseholdId(UUID.randomUUID()))).isFalse();
        assertThatThrownBy(() -> ShoppingList.create(ShoppingListId.newId(), null, new ListName("courses")))
                .isInstanceOf(InvalidValueException.class);
    }
}
