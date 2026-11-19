package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.export;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.Authorship;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.export.JdbcAuthorship;
import io.micronaut.context.annotation.Factory;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/** Auteurs des objets du domaine vehicle (table authorship de son schéma). */
@Factory
class VehicleAuthorshipFactory {

    @Singleton
    @Named("vehicle")
    Authorship vehicleAuthorship(@Named("vehicle") JdbcOperations jdbc) {
        return new JdbcAuthorship(jdbc);
    }
}
