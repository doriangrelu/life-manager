package io.github.doriangrelu.lifemanager.shopping;

import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Migrations des données de courses : V2 range les articles d'une liste unique (V1) dans la sous-liste
 * « courses » de leur foyer ; V3 enregistre les noms en minuscules sans espaces superflus.
 */
class ShoppingMigrationTest extends IntegrationTestSupport {

    // Un schéma à part par test : celui de l'application est déjà migré en dernière version.

    @Test
    void existing_items_move_to_the_groceries_list() throws SQLException {
        var schema = "shopping_migration_lists";
        var household = UUID.randomUUID();
        migrate(schema, "1");
        try (var connection = connect(schema)) {
            execute(connection, "INSERT INTO household_member VALUES ('%s', '%s', '%s')"
                    .formatted(UUID.randomUUID(), household, UUID.randomUUID()));
            execute(connection, "INSERT INTO shopping_item VALUES ('%s', '%s', '  Lait  ', NULL, '%s', now())"
                    .formatted(UUID.randomUUID(), household, UUID.randomUUID()));
        }

        migrate(schema, "latest");

        try (var connection = connect(schema)) {
            assertThat(strings(connection, "SELECT name FROM shopping_list ORDER BY name"))
                    .containsExactly("bricolage", "courses", "vêtements");
            assertThat(strings(connection, """
                    SELECT list.name || ':' || item.label FROM shopping_item item
                    JOIN shopping_list list ON list.id = item.list_id""")).containsExactly("courses:lait");
        }
    }

    @Test
    void a_name_already_taken_once_normalized_does_not_break_the_migration() throws SQLException {
        var schema = "shopping_migration_names";
        var household = UUID.randomUUID();
        migrate(schema, "2");
        try (var connection = connect(schema)) {
            // « Jardin » et « jardin  » coexistent avant V3 (l'unicité ignore la casse, pas les espaces).
            execute(connection, "INSERT INTO shopping_list VALUES ('%s', '%s', 'Jardin')".formatted(UUID.randomUUID(), household));
            execute(connection, "INSERT INTO shopping_list VALUES ('%s', '%s', 'jardin  ')".formatted(UUID.randomUUID(), household));
        }

        migrate(schema, "latest");

        try (var connection = connect(schema)) {
            // « Jardin » est converti ; « jardin  » ne peut pas l'être sans doublon : il reste tel quel.
            assertThat(strings(connection, "SELECT name FROM shopping_list ORDER BY name"))
                    .containsExactly("jardin", "jardin  ");
        }
    }

    private static void migrate(String schema, String version) {
        Flyway.configure()
                .dataSource(postgres().getJdbcUrl(), postgres().getUsername(), postgres().getPassword())
                .schemas(schema)
                .locations("classpath:db/migration/shopping")
                .target(version)
                .load()
                .migrate();
    }

    private static Connection connect(String schema) throws SQLException {
        var connection = DriverManager.getConnection(postgres().getJdbcUrl(), postgres().getUsername(),
                postgres().getPassword());
        connection.setSchema(schema);
        return connection;
    }

    private static void execute(Connection connection, String sql) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static List<String> strings(Connection connection, String sql) throws SQLException {
        var values = new ArrayList<String>();
        try (var statement = connection.createStatement(); var rows = statement.executeQuery(sql)) {
            while (rows.next()) {
                values.add(rows.getString(1));
            }
        }
        return values;
    }
}
