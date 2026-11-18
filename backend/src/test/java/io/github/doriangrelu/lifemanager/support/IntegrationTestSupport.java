package io.github.doriangrelu.lifemanager.support;

import io.micronaut.test.support.TestPropertyProvider;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base des tests d'intégration : un PostgreSQL (Testcontainers) et un faux Keycloak,
 * partagés par tous les tests, branchés sur l'application.
 * Les sous-classes doivent être annotées {@code @TestInstance(PER_CLASS)}.
 */
public abstract class IntegrationTestSupport implements TestPropertyProvider {

    protected static final FakeKeycloak KEYCLOAK = startKeycloak();
    private static final PostgreSQLContainer POSTGRES = startPostgres();
    /** Dossier temporaire des fichiers envoyés pendant les tests. */
    protected static final Path STORAGE_ROOT = temporaryStorage();

    @Override
    public Map<String, String> getProperties() {
        var properties = new HashMap<String, String>();
        // Toutes les datasources de domaine pointent sur le même PostgreSQL de test.
        for (String domain : List.of("identity", "household", "budget", "vehicle", "shopping", "reminder")) {
            properties.put("datasources." + domain + ".url", POSTGRES.getJdbcUrl());
            properties.put("datasources." + domain + ".username", POSTGRES.getUsername());
            properties.put("datasources." + domain + ".password", POSTGRES.getPassword());
        }
        properties.put("micronaut.http.services.keycloak.url", KEYCLOAK.url());
        // Port libre au hasard : les tests passent même si l'application tourne déjà sur 8080.
        properties.put("micronaut.server.port", "-1");
        properties.put("life-manager.storage.root", STORAGE_ROOT.toString());
        return properties;
    }

    /** Le PostgreSQL de test, pour les tests qui y accèdent sans passer par l'application (migrations). */
    protected static PostgreSQLContainer postgres() {
        return POSTGRES;
    }

    private static PostgreSQLContainer startPostgres() {
        var container = new PostgreSQLContainer("postgres:18-alpine")
                .withDatabaseName("lifemanager")
                .withInitScript("db/test-init.sql");
        container.start();
        return container;
    }

    private static Path temporaryStorage() {
        try {
            return Files.createTempDirectory("life-manager-files-");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static FakeKeycloak startKeycloak() {
        try {
            return new FakeKeycloak();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
