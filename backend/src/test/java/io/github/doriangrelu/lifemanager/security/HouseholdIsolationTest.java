package io.github.doriangrelu.lifemanager.security;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.web.router.Router;
import io.micronaut.web.router.UriRouteInfo;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cloisonnement des foyers sur TOUTES les routes : chaque route {@code /api/households/{householdId}/…}
 * est appelée par un utilisateur qui n'est pas membre du foyer. Aucune ne doit réussir ni laisser fuiter
 * une donnée du foyer. Une nouvelle route est couverte automatiquement.
 */
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HouseholdIsolationTest extends IntegrationTestSupport {

    private static final Pattern VARIABLE = Pattern.compile("\\{([a-zA-Z]+)[^}]*}");
    private static final String SECRET_NAME = "maison secrète du test";

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    Router router;

    @Test
    void a_stranger_never_reaches_another_household() {
        var owner = newUser();
        var stranger = newUser();
        var households = new HouseholdApi(client.toBlocking());
        var household = households.create(owner, SECRET_NAME).id();
        households.create(stranger, "Chez moi");

        var routes = householdRoutes();
        var leaks = new ArrayList<String>();
        for (var route : routes) {
            var method = route.getHttpMethod();
            var uri = expand(route.getUriMatchTemplate().toString(), household);
            var answer = call(method, uri, stranger);
            if (answer.status() < 400 || answer.status() >= 500 || answer.body().contains("secrète")) {
                leaks.add(method + " " + uri + " → " + answer.status());
            }
            // Une lecture du foyer lui-même (sans autre identifiant) doit répondre « introuvable ».
            if (method == HttpMethod.GET && !uri.matches(".*/[0-9a-f-]{36}/.*[0-9a-f-]{36}.*") && answer.status() != 404) {
                leaks.add(method + " " + uri + " → " + answer.status() + " (404 attendu)");
            }
        }

        assertThat(routes).as("routes de foyer trouvées").hasSizeGreaterThan(30);
        assertThat(leaks).as("routes qui laissent passer un non-membre").isEmpty();
    }

    private List<UriRouteInfo<?, ?>> householdRoutes() {
        return router.uriRoutes()
                .filter(route -> route.getUriMatchTemplate().toString().contains("{householdId}"))
                .toList();
    }

    /** Le foyer visé est réel ; les autres identifiants sont inventés, le mois est le mois courant. */
    private static String expand(String template, UUID household) {
        var matcher = VARIABLE.matcher(template);
        var uri = new StringBuilder();
        while (matcher.find()) {
            var value = switch (matcher.group(1)) {
                case "householdId" -> household.toString();
                case "month" -> YearMonth.now().toString();
                default -> UUID.randomUUID().toString();
            };
            matcher.appendReplacement(uri, value);
        }
        matcher.appendTail(uri);
        return uri.toString();
    }

    private Answer call(HttpMethod method, String uri, String token) {
        var request = HttpRequest.create(method, uri).bearerAuth(token);
        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            request = request.contentType(MediaType.APPLICATION_JSON_TYPE).body("{}");
        }
        try {
            var response = client.toBlocking().exchange(request, String.class);
            return new Answer(response.code(), response.getBody().orElse(""));
        } catch (HttpClientResponseException e) {
            return new Answer(e.code(), e.getResponse().getBody(String.class).orElse(""));
        }
    }

    private record Answer(int status, String body) {
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
