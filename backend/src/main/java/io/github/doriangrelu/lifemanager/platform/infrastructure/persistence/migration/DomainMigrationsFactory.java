package io.github.doriangrelu.lifemanager.platform.infrastructure.persistence.migration;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.flyway.FlywayConfigurationCustomizer;
import io.micronaut.flyway.FlywayConfigurationProperties;

/** Un réglage Flyway par datasource (Micronaut le retrouve grâce au nom de la datasource). */
@Factory
class DomainMigrationsFactory {

    @EachBean(FlywayConfigurationProperties.class)
    FlywayConfigurationCustomizer domainMigrations(FlywayConfigurationProperties properties) {
        return new DomainMigrationsCustomizer(properties.getNameQualifier());
    }
}
