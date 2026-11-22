package io.github.doriangrelu.lifemanager.project;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Lot 8 : projets, tâches (et leurs rappels), dépenses rattachées et projets hors budget. */
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectsIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));
    private static final Argument<Map<String, Object>> JSON = Argument.mapOf(String.class, Object.class);
    private static final Argument<List<Map<String, Object>>> JSON_LIST = Argument.listOf(JSON);

    @Inject
    @Client("/")
    HttpClient client;

    BlockingHttpClient http;
    HouseholdApi households;

    @BeforeAll
    void setUp() {
        http = client.toBlocking();
        households = new HouseholdApi(http);
    }

    @Test
    void a_project_has_tasks_with_reminders_and_progress() {
        var home = newHome();
        var project = addProject(home, Map.of("name", "Salle de bain", "color", "TEAL", "plannedCents", 300_000));
        assertThat(project).containsEntry("status", "ACTIVE").containsEntry("countsInBudget", true);
        var projectId = id(project);

        var soon = addTask(home, projectId, Map.of("title", "Choisir le carrelage", "assigneeId", home.ownerId,
                "dueOn", TODAY.plusDays(3).toString()));
        addTask(home, projectId, Map.of("title", "Demander des devis", "dueOn", TODAY.plusDays(30).toString()));
        assertThat(reminderSubjects(home)).containsExactly("Choisir le carrelage · Salle de bain");

        post(home, "/projects/" + projectId + "/tasks/" + id(soon) + "/done", Map.of());
        assertThat(reminderSubjects(home)).isEmpty();
        var listed = get(home, "/projects", JSON_LIST).getFirst();
        assertThat(listed).containsEntry("tasksDone", 1).containsEntry("tasksTotal", 2);

        // Décochée : de nouveau rappelée ; projet terminé : plus de rappel.
        http.exchange(HttpRequest.DELETE(route(home, "/projects/" + projectId + "/tasks/" + id(soon) + "/done"))
                .bearerAuth(home.owner));
        assertThat(reminderSubjects(home)).hasSize(1);
        put(home, "/projects/" + projectId, Map.of("name", "Salle de bain", "color", "TEAL", "status", "DONE"));
        assertThat(reminderSubjects(home)).isEmpty();
    }

    @Test
    void invalid_inputs_are_refused_and_households_stay_apart() {
        var home = newHome();
        var stranger = newHome();
        assertThat(failure(() -> addProject(home, Map.of("name", "", "color", "TEAL"))).getStatus().getCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(failure(() -> addProject(home, Map.of("name", "X", "color", "VIOLET"))).getStatus().getCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(failure(() -> addProject(home, Map.of("name", "X", "color", "TEAL",
                "startOn", "2027-05-01", "targetOn", "2027-04-01"))).getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        var projectId = id(addProject(home, Map.of("name", "Vacances", "color", "AMBER")));
        assertThat(failure(() -> addTask(home, projectId, Map.of("title", "Réserver",
                "assigneeId", stranger.ownerId))).getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        // Un autre foyer ne voit ni le projet ni ses dépenses.
        assertThat(failure(() -> http.exchange(HttpRequest.GET("/api/households/" + home.household + "/projects/"
                + projectId).bearerAuth(stranger.owner))).getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
        assertThat(failure(() -> get(stranger, "/projects/" + projectId, JSON)).getStatus().getCode())
                .isEqualTo(HttpStatus.NOT_FOUND.getCode());
        assertThat(failure(() -> get(stranger, "/projects/" + projectId + "/expenses", JSON)).getStatus().getCode())
                .isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }

    @Test
    void project_expenses_count_in_the_budget_unless_the_project_is_off_budget() {
        var home = newHome();
        var works = id(addProject(home, Map.of("name", "Salle de bain", "color", "TEAL")));
        var trip = id(addProject(home, Map.of("name", "Vacances", "color", "AMBER", "countsInBudget", false)));
        addExpense(home, 40_000, works);
        addExpense(home, 80_000, trip);
        addExpense(home, 1_000, null);

        var month = month(home);
        assertThat(month.get("totalCents")).isEqualTo(41_000);
        List<Map<String, Object>> expenses = list(month.get("expenses"));
        assertThat(expenses).filteredOn(e -> Boolean.TRUE.equals(e.get("offBudget"))).hasSize(1)
                .first().satisfies(e -> assertThat(e).containsEntry("projectId", trip.toString()));
        assertThat(get(home, "/projects/" + trip + "/expenses", JSON)).containsEntry("totalCents", 80_000);

        // Rattacher une dépense à un projet terminé est refusé.
        put(home, "/projects/" + works, Map.of("name", "Salle de bain", "color", "TEAL", "status", "DONE"));
        assertThat(failure(() -> addExpense(home, 500, works)).getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());

        // Projet supprimé : sa dépense redevient une dépense normale, comptée.
        http.exchange(HttpRequest.DELETE(route(home, "/projects/" + trip)).bearerAuth(home.owner));
        assertThat(month(home).get("totalCents")).isEqualTo(121_000);
    }

    // ---------- outils ----------

    private record Home(String owner, UUID ownerId, UUID household) {
    }

    private Home newHome() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        var ownerId = UUID.fromString(String.valueOf(http.retrieve(HttpRequest.GET("/api/whoami").bearerAuth(token),
                Map.class).get("userId")));
        return new Home(token, ownerId, households.create(token, "Maison").id());
    }

    private Map<String, Object> addProject(Home home, Map<String, Object> body) {
        return http.retrieve(HttpRequest.POST(route(home, "/projects"), body).bearerAuth(home.owner), JSON);
    }

    private Map<String, Object> addTask(Home home, UUID project, Map<String, Object> body) {
        return http.retrieve(HttpRequest.POST(route(home, "/projects/" + project + "/tasks"), body)
                .bearerAuth(home.owner), JSON);
    }

    private void addExpense(Home home, long cents, UUID project) {
        List<Map<String, Object>> categories = get(home, "/expense-categories", JSON_LIST);
        var body = new HashMap<String, Object>();
        body.put("amountCents", cents);
        body.put("categoryId", categories.getFirst().get("id"));
        body.put("spentOn", TODAY.toString());
        body.put("paidBy", home.ownerId);
        body.put("projectId", project);
        http.exchange(HttpRequest.POST(route(home, "/expenses"), body).bearerAuth(home.owner));
    }

    private Map<String, Object> month(Home home) {
        return get(home, "/budget/" + TODAY.toString().substring(0, 7), JSON);
    }

    private List<String> reminderSubjects(Home home) {
        return get(home, "/reminders", JSON_LIST).stream()
                .filter(r -> "TASK".equals(r.get("kind")))
                .map(r -> String.valueOf(r.get("subject")))
                .toList();
    }

    private <T> T get(Home home, String path, Argument<T> type) {
        return http.retrieve(HttpRequest.GET(route(home, path)).bearerAuth(home.owner), type);
    }

    private void post(Home home, String path, Object body) {
        http.exchange(HttpRequest.POST(route(home, path), body).bearerAuth(home.owner));
    }

    private void put(Home home, String path, Object body) {
        http.exchange(HttpRequest.PUT(route(home, path), body).bearerAuth(home.owner));
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> list(Object rows) {
        return (List<Map<String, Object>>) rows;
    }

    private static UUID id(Map<String, Object> row) {
        return UUID.fromString(String.valueOf(row.get("id")));
    }

    private static String route(Home home, String path) {
        return "/api/households/" + home.household + path;
    }
}
