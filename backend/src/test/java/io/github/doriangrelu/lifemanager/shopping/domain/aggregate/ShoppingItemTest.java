package io.github.doriangrelu.lifemanager.shopping.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemLabel;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShoppingItemTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-16T10:00:00Z"), ZoneId.of("Europe/Paris"));

    @Test
    void a_label_has_1_to_60_characters() {
        assertThat(new ItemLabel(" 2 l de lait ").value()).isEqualTo("2 l de lait");
        assertThatThrownBy(() -> new ItemLabel("")).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new ItemLabel("a".repeat(61))).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void a_new_item_is_not_checked() {
        assertThat(milk().isChecked()).isFalse();
    }

    @Test
    void checking_twice_keeps_the_first_check_time() {
        var item = milk();

        item.check(CLOCK);
        item.check(Clock.offset(CLOCK, Duration.ofMinutes(5)));

        assertThat(item.checkedAt()).contains(CLOCK.instant());
    }

    @Test
    void unchecking_is_idempotent() {
        var item = milk();
        item.check(CLOCK);

        item.uncheck();
        item.uncheck();

        assertThat(item.isChecked()).isFalse();
    }

    private static ShoppingItem milk() {
        return ShoppingItem.add(ShoppingItemId.newId(), new HouseholdId(UUID.randomUUID()), new ItemLabel("Lait"),
                new MemberId(UUID.randomUUID()), CLOCK);
    }
}
