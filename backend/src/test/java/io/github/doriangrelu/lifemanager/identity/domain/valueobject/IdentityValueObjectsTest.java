package io.github.doriangrelu.lifemanager.identity.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentityValueObjectsTest {

    @Test
    void user_id_is_built_from_a_keycloak_subject() {
        var uuid = UUID.randomUUID();

        assertThat(UserId.of(uuid.toString()).value()).isEqualTo(uuid);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"pas-un-uuid"})
    void user_id_rejects_anything_but_a_uuid(String subject) {
        assertThatThrownBy(() -> UserId.of(subject)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void email_is_normalized_to_lower_case() {
        assertThat(new EmailAddress(" Alice@Example.ORG ").value()).isEqualTo("alice@example.org");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"alice", "alice@", "@example.org", "alice@example", "al ice@example.org"})
    void email_rejects_malformed_addresses(String value) {
        assertThatThrownBy(() -> new EmailAddress(value)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void display_name_is_trimmed_and_limited_to_100_characters() {
        assertThat(new DisplayName("  Alice ").value()).isEqualTo("Alice");
        assertThat(new DisplayName("a".repeat(100)).value()).hasSize(100);
        assertThatThrownBy(() -> new DisplayName("a".repeat(101))).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new DisplayName(" ")).isInstanceOf(InvalidValueException.class);
    }
}
