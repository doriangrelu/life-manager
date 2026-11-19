package io.github.doriangrelu.lifemanager.identity.domain.event;

import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;

/** L'utilisateur a supprimé son compte : son profil est effacé, ses foyers doivent être quittés. */
public record UserDeleted(UserId userId, Instant occurredAt) implements DomainEvent {
}
