package io.github.doriangrelu.lifemanager.kernel.domain.validation;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequireTest {

    @Test
    void not_blank_strips_surrounding_spaces() {
        assertThat(Require.notBlank("  Maison ", "nom")).isEqualTo("Maison");
    }

    @Test
    void not_blank_rejects_null_and_blank() {
        assertThatThrownBy(() -> Require.notBlank(null, "nom")).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> Require.notBlank("   ", "nom"))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage("nom ne doit pas être vide");
    }

    @Test
    void max_length_rejects_too_long_text() {
        assertThat(Require.maxLength("abc", 3, "nom")).isEqualTo("abc");
        assertThatThrownBy(() -> Require.maxLength("abcd", 3, "nom")).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void not_null_rejects_null() {
        assertThatThrownBy(() -> Require.notNull(null, "date")).isInstanceOf(InvalidValueException.class);
    }
}
