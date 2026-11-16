package io.github.doriangrelu.lifemanager.platform;

import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ErrorHandlingTest extends IntegrationTestSupport {

    private static final String TOKEN = "token-error-tests";

    @Inject
    @Client("/")
    HttpClient client;

    @BeforeAll
    void registerToken() {
        KEYCLOAK.activeToken(TOKEN, "carol@example.org", "Carol");
    }

    @ParameterizedTest
    @CsvSource({"invalid, 400, valeur invalide", "not-found, 404, introuvable", "conflict, 409, conflit"})
    void business_errors_become_http_errors_with_their_message(String path, int status, String message) {
        var error = catchThrowableOfType(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.GET("/test-probe/" + path).bearerAuth(TOKEN), String.class));

        assertThat(error.code()).isEqualTo(status);
        assertThat(error.getResponse().getBody(String.class)).hasValueSatisfying(
                body -> assertThat(body).contains(message));
    }
}
