package io.github.doriangrelu.lifemanager.household.domain.event;

import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;

/** Un utilisateur a rejoint un foyer existant. */
public record MemberJoined(HouseholdId householdId, MemberId member, Instant occurredAt) implements DomainEvent {
}
