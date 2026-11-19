package io.github.doriangrelu.lifemanager.platform;

import io.github.doriangrelu.lifemanager.platform.infrastructure.web.version.VersionResponse;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;

/** La version de l'API se lit sans jeton (« dev » hors déploiement). */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VersionIntegrationTest extends IntegrationTestSupport {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void version_is_public() {
        var response = client.toBlocking().retrieve(HttpRequest.GET("/api/version"), VersionResponse.class);
        assertThat(response.version()).isEqualTo("dev");
    }
}
