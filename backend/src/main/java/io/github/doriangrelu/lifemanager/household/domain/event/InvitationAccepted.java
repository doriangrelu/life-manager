package io.github.doriangrelu.lifemanager.household.domain.event;

import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;

/** Un code d'invitation a été utilisé (il ne peut plus servir). */
public record InvitationAccepted(InvitationId invitationId, HouseholdId householdId, MemberId acceptedBy,
                                 Instant occurredAt) implements DomainEvent {
}
