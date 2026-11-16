package io.github.doriangrelu.lifemanager.platform;

import io.github.doriangrelu.lifemanager.support.PostgresTestSupport;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HealthEndpointTest extends PostgresTestSupport {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void health_is_public() {
        var response = client.toBlocking().exchange(HttpRequest.GET("/health"));

        assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
    }

    @Test
    void api_requires_authentication() {
        var error = catchThrowableOfType(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.GET("/api/whoami")));

        assertThat(error.code()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
    }
}
