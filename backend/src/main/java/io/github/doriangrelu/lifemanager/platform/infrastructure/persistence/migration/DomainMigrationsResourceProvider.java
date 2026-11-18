package io.github.doriangrelu.lifemanager.platform.infrastructure.persistence.migration;

import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.ResourceProvider;
import org.flywaydb.core.api.resource.LoadableResource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Ne laisse voir à Flyway que les migrations des dossiers d'un domaine.
 * Utile en natif : Micronaut y fournit un catalogue unique contenant les migrations de tous les domaines.
 */
final class DomainMigrationsResourceProvider implements ResourceProvider {

    private final ResourceProvider all;
    private final List<String> folders;

    DomainMigrationsResourceProvider(ResourceProvider all, Location... locations) {
        this.all = all;
        // « db/migration/budget » devient « /db/migration/budget/ » : évite de confondre deux domaines au nom proche.
        this.folders = Arrays.stream(locations).map(location -> "/" + location.getPath() + "/").toList();
    }

    @Override
    public LoadableResource getResource(String name) {
        var resource = all.getResource(name);
        return resource != null && belongsToDomain(resource) ? resource : null;
    }

    @Override
    public Collection<LoadableResource> getResources(String prefix, String... suffixes) {
        return all.getResources(prefix, suffixes).stream().filter(this::belongsToDomain).toList();
    }

    private boolean belongsToDomain(LoadableResource resource) {
        var path = resource.getAbsolutePath().replace('\\', '/');
        return folders.stream().anyMatch(path::contains);
    }
}
