package io.github.doriangrelu.lifemanager.support;

import io.micronaut.test.support.TestPropertyProvider;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

/**
 * Base des tests d'intégration : un PostgreSQL (Testcontainers) et un faux Keycloak,
 * partagés par tous les tests, branchés sur l'application.
 * Les sous-classes doivent être annotées {@code @TestInstance(PER_CLASS)}.
 */
public abstract class IntegrationTestSupport implements TestPropertyProvider {

    protected static final FakeKeycloak KEYCLOAK = startKeycloak();
    private static final PostgreSQLContainer POSTGRES = startPostgres();

    @Override
    public Map<String, String> getProperties() {
        return Map.of(
                "datasources.identity.url", POSTGRES.getJdbcUrl(),
                "datasources.identity.username", POSTGRES.getUsername(),
                "datasources.identity.password", POSTGRES.getPassword(),
                "datasources.household.url", POSTGRES.getJdbcUrl(),
                "datasources.household.username", POSTGRES.getUsername(),
                "datasources.household.password", POSTGRES.getPassword(),
                "micronaut.http.services.keycloak.url", KEYCLOAK.url());
    }

    private static PostgreSQLContainer startPostgres() {
        var container = new PostgreSQLContainer("postgres:18-alpine")
                .withDatabaseName("lifemanager")
                .withInitScript("db/test-init.sql");
        container.start();
        return container;
    }

    private static FakeKeycloak startKeycloak() {
        try {
            return new FakeKeycloak();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
