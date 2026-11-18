package io.github.doriangrelu.lifemanager.platform.infrastructure.persistence.migration;

import io.micronaut.flyway.FlywayConfigurationCustomizer;
import org.flywaydb.core.api.configuration.FluentConfiguration;

/**
 * Chaque domaine n'applique que ses propres migrations, y compris dans l'exécutable natif,
 * où Micronaut fournit un catalogue unique de toutes les migrations.
 * Sans effet en JVM : aucun catalogue n'est fourni et Flyway lit directement le dossier du domaine.
 *
 * @param name nom de la datasource du domaine
 */
record DomainMigrationsCustomizer(String name) implements FlywayConfigurationCustomizer {

    @Override
    public void customizeFluentConfiguration(FluentConfiguration configuration) {
        var all = configuration.getResourceProvider();
        if (all != null) {
            configuration.resourceProvider(new DomainMigrationsResourceProvider(all, configuration.getLocations()));
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
