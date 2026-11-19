package io.github.doriangrelu.lifemanager.reminder;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.PersonalReminderRequest;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.PersonalReminderResponse;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.ReminderResponse;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Rappels saisis par les membres : ajout, modification, « c'est fait », suppression, cloisonnement. */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonalRemindersIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));

    @Inject
    @Client("/")
    HttpClient client;

    BlockingHttpClient http;
    String owner;
    UUID household;

    @BeforeEach
    void newHousehold() {
        http = client.toBlocking();
        owner = newUser();
        household = new HouseholdApi(http).create(owner, "Maison").id();
    }

    @Test
    void a_saved_reminder_joins_the_household_reminders_within_its_notice_period() {
        add(new PersonalReminderRequest("Ramonage", "Appeler M. Martin", TODAY.plusDays(5), "YEARLY", "ONE_WEEK"));
        add(new PersonalReminderRequest("Impôts", null, TODAY.plusDays(20), "NONE", "ONE_WEEK"));

        assertThat(list()).extracting(PersonalReminderResponse::title).containsExactly("Ramonage", "Impôts");
        assertThat(reminders()).extracting(ReminderResponse::kind, ReminderResponse::subject, ReminderResponse::daysLeft)
                .containsExactly(org.assertj.core.groups.Tuple.tuple("PERSONAL", "Ramonage", 5L));
    }

    @Test
    void done_removes_a_one_off_reminder_and_moves_a_recurring_one_forward() {
        var once = add(new PersonalReminderRequest("Mutuelle", null, TODAY.minusDays(3), "NONE", "SAME_DAY"));
        var monthly = add(new PersonalReminderRequest("Relevé", null, TODAY.minusDays(3), "MONTHLY", "SAME_DAY"));

        done(once.id());
        done(monthly.id());

        assertThat(list()).extracting(PersonalReminderResponse::title, PersonalReminderResponse::dueOn)
                .containsExactly(org.assertj.core.groups.Tuple.tuple("Relevé", TODAY.minusDays(3).plusMonths(1)));
    }

    @Test
    void a_reminder_can_be_changed_and_deleted() {
        var saved = add(new PersonalReminderRequest("Ramonage", null, TODAY.plusDays(60), "NONE", "ONE_MONTH"));

        var changed = http.retrieve(HttpRequest.PUT(base() + "/" + saved.id(),
                new PersonalReminderRequest("Ramonage du poêle", "Avant l'hiver", TODAY.plusDays(10), "YEARLY",
                        "ONE_MONTH")).bearerAuth(owner), PersonalReminderResponse.class);
        assertThat(changed.title()).isEqualTo("Ramonage du poêle");
        assertThat(changed.note()).isEqualTo("Avant l'hiver");

        http.exchange(HttpRequest.DELETE(base() + "/" + saved.id()).bearerAuth(owner));
        assertThat(list()).isEmpty();
    }

    @Test
    void invalid_reminders_and_strangers_are_refused() {
        var stranger = newUser();
        var saved = add(new PersonalReminderRequest("Ramonage", null, TODAY, "NONE", "SAME_DAY"));

        assertThat(code(new PersonalReminderRequest(" ", null, TODAY, "NONE", "SAME_DAY"))).isEqualTo(400);
        assertThat(code(new PersonalReminderRequest("a".repeat(61), null, TODAY, "NONE", "SAME_DAY"))).isEqualTo(400);
        assertThat(code(new PersonalReminderRequest("Impôts", null, TODAY, "WEEKLY", "SAME_DAY"))).isEqualTo(400);
        assertThat(code(new PersonalReminderRequest("Impôts", null, TODAY, "NONE", "TWO_DAYS"))).isEqualTo(400);
        assertThat(failure(() -> http.retrieve(HttpRequest.GET(base()).bearerAuth(stranger))).code()).isEqualTo(404);
        assertThat(failure(() -> http.exchange(HttpRequest.POST(base() + "/" + saved.id() + "/done", "")
                .bearerAuth(stranger))).code()).isEqualTo(404);
        assertThat(failure(() -> http.exchange(HttpRequest.DELETE(base() + "/" + UUID.randomUUID())
                .bearerAuth(owner))).code()).isEqualTo(404);
    }

    private PersonalReminderResponse add(PersonalReminderRequest request) {
        return http.retrieve(HttpRequest.POST(base(), request).bearerAuth(owner), PersonalReminderResponse.class);
    }

    private int code(PersonalReminderRequest request) {
        return failure(() -> add(request)).code();
    }

    private void done(UUID reminder) {
        http.exchange(HttpRequest.POST(base() + "/" + reminder + "/done", "").bearerAuth(owner));
    }

    private List<PersonalReminderResponse> list() {
        return http.retrieve(HttpRequest.GET(base()).bearerAuth(owner), Argument.listOf(PersonalReminderResponse.class));
    }

    private List<ReminderResponse> reminders() {
        return http.retrieve(HttpRequest.GET("/api/households/" + household + "/reminders").bearerAuth(owner),
                Argument.listOf(ReminderResponse.class));
    }

    private String base() {
        return "/api/households/" + household + "/personal-reminders";
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
