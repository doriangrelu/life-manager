package io.github.doriangrelu.lifemanager.household.domain.event;

import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;

/** Un membre a quitté le foyer ou en a été retiré ; {@code owner} est le propriétaire à ce moment-là. */
public record MemberLeft(HouseholdId householdId, MemberId member, MemberId owner, Instant occurredAt)
        implements DomainEvent {
}
