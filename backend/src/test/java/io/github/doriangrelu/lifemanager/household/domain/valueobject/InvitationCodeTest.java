package io.github.doriangrelu.lifemanager.household.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvitationCodeTest {

    @Test
    void input_is_case_insensitive_and_trimmed() {
        assertThat(new InvitationCode(" abcd2345 ").value()).isEqualTo("ABCD2345");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"ABC", "ABCD23456", "ABCD234O", "ABCD2341", "ABCD-234"})
    void malformed_codes_are_rejected(String value) {
        assertThatThrownBy(() -> new InvitationCode(value)).isInstanceOf(InvalidValueException.class);
    }

    @RepeatedTest(20)
    void random_codes_are_always_valid() {
        var code = InvitationCode.random(new SecureRandom());

        assertThat(new InvitationCode(code.value())).isEqualTo(code);
    }
}
