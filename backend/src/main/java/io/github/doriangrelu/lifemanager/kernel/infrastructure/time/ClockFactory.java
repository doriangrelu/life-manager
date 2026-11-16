package io.github.doriangrelu.lifemanager.kernel.infrastructure.time;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;

/**
 * Fournit l'horloge utilisée par les use cases : on injecte {@link Clock}
 * au lieu d'appeler {@code Instant.now()}, pour pouvoir fixer l'heure dans les tests.
 */
@Factory
class ClockFactory {

    @Singleton
    Clock clock(@Value("${life-manager.time-zone}") String timeZone) {
        // Fuseau des utilisateurs : LocalDate.now(clock) donne leur « aujourd'hui » (les Instant restent en UTC).
        // Précision à la microseconde, comme PostgreSQL : une date relue en base est identique à celle écrite.
        return Clock.tick(Clock.system(ZoneId.of(timeZone)), Duration.ofNanos(1_000));
    }
}
