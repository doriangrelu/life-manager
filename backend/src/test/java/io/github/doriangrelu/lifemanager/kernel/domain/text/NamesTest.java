package io.github.doriangrelu.lifemanager.kernel.domain.text;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class NamesTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
        "Lait|lait",
        "'  LAIT   demi-écrémé  '|lait demi-écrémé",
        "Vêtements|vêtements",
        "ÉPICERIE fine|épicerie fine",
    })
    void storesTrimmedLowerCaseText(String typed, String stored) {
        assertThat(Names.normalized(typed)).isEqualTo(stored);
    }

    @Test
    void replacesTabsAndLineBreaksWithASingleSpace() {
        assertThat(Names.normalized("pain\t\ncomplet")).isEqualTo("pain complet");
    }
}
