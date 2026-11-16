package io.github.doriangrelu.lifemanager.support;

import io.micronaut.test.support.TestPropertyProvider;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Map;

/**
 * Base des tests d'intégration : démarre un PostgreSQL partagé par tous les tests
 * et y branche les datasources de chaque domaine.
 * Les sous-classes doivent être annotées {@code @TestInstance(PER_CLASS)}.
 */
public abstract class PostgresTestSupport implements TestPropertyProvider {

    private static final PostgreSQLContainer POSTGRES = startPostgres();

    @Override
    public Map<String, String> getProperties() {
        return Map.of(
                "datasources.identity.url", POSTGRES.getJdbcUrl(),
                "datasources.identity.username", POSTGRES.getUsername(),
                "datasources.identity.password", POSTGRES.getPassword(),
                "datasources.household.url", POSTGRES.getJdbcUrl(),
                "datasources.household.username", POSTGRES.getUsername(),
                "datasources.household.password", POSTGRES.getPassword());
    }

    private static PostgreSQLContainer startPostgres() {
        var container = new PostgreSQLContainer("postgres:18-alpine")
                .withDatabaseName("lifemanager")
                .withInitScript("db/test-init.sql");
        container.start();
        return container;
    }
}
