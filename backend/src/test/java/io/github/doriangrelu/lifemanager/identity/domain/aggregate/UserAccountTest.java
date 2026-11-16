package io.github.doriangrelu.lifemanager.identity.domain.aggregate;

import io.github.doriangrelu.lifemanager.identity.domain.event.UserProfileChanged;
import io.github.doriangrelu.lifemanager.identity.domain.event.UserRegistered;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.DisplayName;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.EmailAddress;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserAccountTest {

    private static final UserId ID = new UserId(UUID.randomUUID());
    private static final EmailAddress EMAIL = new EmailAddress("alice@example.org");
    private static final DisplayName NAME = new DisplayName("Alice");
    private static final Instant NOW = Instant.parse("2026-11-16T10:00:00Z");

    @Test
    void registering_records_a_user_registered_event() {
        var account = UserAccount.register(ID, EMAIL, NAME, NOW);

        assertThat(account.pullEvents()).containsExactly(new UserRegistered(ID, EMAIL, NAME, NOW));
    }

    @Test
    void restoring_records_no_event() {
        var account = UserAccount.restore(ID, EMAIL, NAME, NOW);

        assertThat(account.pullEvents()).isEmpty();
    }

    @Test
    void updating_with_new_values_records_a_profile_changed_event() {
        var account = UserAccount.restore(ID, EMAIL, NAME, NOW);
        var newName = new DisplayName("Alice Martin");

        account.updateProfile(EMAIL, newName, NOW);

        assertThat(account.displayName()).isEqualTo(newName);
        assertThat(account.pullEvents()).containsExactly(new UserProfileChanged(ID, EMAIL, newName, NOW));
    }

    @Test
    void updating_with_same_values_records_nothing() {
        var account = UserAccount.restore(ID, EMAIL, NAME, NOW);

        account.updateProfile(EMAIL, NAME, NOW);

        assertThat(account.pullEvents()).isEmpty();
    }
}
