package io.github.doriangrelu.lifemanager.platform.infrastructure.security.introspection;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class IntrospectionTest {

    @Test
    void inactive_response_is_never_valid() {
        var introspection = Introspection.fromResponse(Map.of("active", false));

        assertThat(introspection.isValidFor("life-manager-api")).isFalse();
    }

    @Test
    void audience_can_be_a_single_string_or_a_list() {
        var single = Introspection.fromResponse(Map.of("active", true, "sub", "s", "aud", "life-manager-api"));
        var list = Introspection.fromResponse(Map.of("active", true, "sub", "s", "aud", List.of("account", "life-manager-api")));

        assertThat(single.isValidFor("life-manager-api")).isTrue();
        assertThat(list.isValidFor("life-manager-api")).isTrue();
        assertThat(list.isValidFor("other-app")).isFalse();
    }

    @Test
    void display_name_falls_back_to_username_then_email() {
        var withUsername = Introspection.fromResponse(Map.of("active", true, "preferred_username", "alice", "email", "a@x.org"));
        var withEmailOnly = Introspection.fromResponse(Map.of("active", true, "email", "a@x.org"));

        assertThat(withUsername.displayName()).isEqualTo("alice");
        assertThat(withEmailOnly.displayName()).isEqualTo("a@x.org");
    }
}
