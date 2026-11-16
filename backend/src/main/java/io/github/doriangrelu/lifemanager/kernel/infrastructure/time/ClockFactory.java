package io.github.doriangrelu.lifemanager.kernel.infrastructure.time;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.Duration;

/**
 * Fournit l'horloge utilisée par les use cases : on injecte {@link Clock}
 * au lieu d'appeler {@code Instant.now()}, pour pouvoir fixer l'heure dans les tests.
 */
@Factory
class ClockFactory {

    @Singleton
    Clock clock() {
        // Précision à la microseconde, comme PostgreSQL : une date relue en base est identique à celle écrite.
        return Clock.tick(Clock.systemUTC(), Duration.ofNanos(1_000));
    }
}
