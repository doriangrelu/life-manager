package io.github.doriangrelu.lifemanager.identity.domain.event;

import io.github.doriangrelu.lifemanager.identity.domain.valueobject.DisplayName;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.EmailAddress;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;

/** L'email ou le nom d'affichage d'un utilisateur a changé dans Keycloak. */
public record UserProfileChanged(UserId userId, EmailAddress email, DisplayName displayName, Instant occurredAt)
        implements DomainEvent {
}
