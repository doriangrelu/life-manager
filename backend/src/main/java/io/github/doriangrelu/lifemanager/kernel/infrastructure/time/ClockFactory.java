package io.github.doriangrelu.lifemanager.kernel.infrastructure.time;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

import java.time.Clock;

/**
 * Fournit l'horloge utilisée par les use cases : on injecte {@link Clock}
 * au lieu d'appeler {@code Instant.now()}, pour pouvoir fixer l'heure dans les tests.
 */
@Factory
class ClockFactory {

    @Singleton
    Clock clock() {
        return Clock.systemUTC();
    }
}
