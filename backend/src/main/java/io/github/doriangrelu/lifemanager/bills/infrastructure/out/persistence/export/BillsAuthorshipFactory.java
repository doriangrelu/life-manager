package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.export;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.Authorship;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.export.JdbcAuthorship;
import io.micronaut.context.annotation.Factory;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/** Auteurs des objets du domaine bills (table authorship de son schéma). */
@Factory
class BillsAuthorshipFactory {

    @Singleton
    @Named("bills")
    Authorship billsAuthorship(@Named("bills") JdbcOperations jdbc) {
        return new JdbcAuthorship(jdbc);
    }
}
