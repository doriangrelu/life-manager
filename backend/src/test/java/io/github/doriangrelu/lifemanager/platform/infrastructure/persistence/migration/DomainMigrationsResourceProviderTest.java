package io.github.doriangrelu.lifemanager.platform.infrastructure.persistence.migration;

import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.ResourceProvider;
import org.flywaydb.core.api.resource.LoadableResource;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DomainMigrationsResourceProviderTest {

    // Catalogue unique, comme celui fourni par Micronaut dans l'exécutable natif.
    private final ResourceProvider all = new ResourceProvider() {
        private final List<LoadableResource> resources = List.of(
                migration("/build/classes/db/migration/budget/V1__create.sql"),
                migration("/build/classes/db/migration/budget-archive/V1__create.sql"),
                migration("/build/classes/db/migration/vehicle/V1__create.sql"));

        @Override
        public LoadableResource getResource(String name) {
            return resources.stream().filter(r -> r.getAbsolutePath().equals(name)).findFirst().orElse(null);
        }

        @Override
        public Collection<LoadableResource> getResources(String prefix, String... suffixes) {
            return resources;
        }
    };

    @Test
    void onlyKeepsMigrationsOfTheDomainFolder() {
        var provider = new DomainMigrationsResourceProvider(all, new Location("classpath:db/migration/budget"));

        assertThat(provider.getResources("V", ".sql"))
                .extracting(LoadableResource::getAbsolutePath)
                .containsExactly("/build/classes/db/migration/budget/V1__create.sql");
    }

    @Test
    void hidesASingleResourceOfAnotherDomain() {
        var provider = new DomainMigrationsResourceProvider(all, new Location("classpath:db/migration/budget"));

        assertThat(provider.getResource("/build/classes/db/migration/vehicle/V1__create.sql")).isNull();
        assertThat(provider.getResource("/build/classes/db/migration/budget/V1__create.sql")).isNotNull();
    }

    private static LoadableResource migration(String path) {
        return new LoadableResource() {
            @Override
            public Reader read() {
                return new StringReader("");
            }

            @Override
            public String getAbsolutePath() {
                return path;
            }

            @Override
            public String getAbsolutePathOnDisk() {
                return path;
            }

            @Override
            public String getFilename() {
                return path.substring(path.lastIndexOf('/') + 1);
            }

            @Override
            public String getRelativePath() {
                return path;
            }
        };
    }
}
