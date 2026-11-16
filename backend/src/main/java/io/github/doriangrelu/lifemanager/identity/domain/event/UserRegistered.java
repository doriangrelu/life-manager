package io.github.doriangrelu.lifemanager.identity.domain.event;

import io.github.doriangrelu.lifemanager.identity.domain.valueobject.DisplayName;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.EmailAddress;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;

/** Un utilisateur utilise l'application pour la première fois : son profil local est créé. */
public record UserRegistered(UserId userId, EmailAddress email, DisplayName displayName, Instant occurredAt)
        implements DomainEvent {
}
