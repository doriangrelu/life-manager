package io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox;

import io.micronaut.serde.annotation.Serdeable;

/** Événement de test, numéroté pour vérifier l'ordre de distribution. */
@Serdeable
public record ProbeEventV1(int number) {
}
