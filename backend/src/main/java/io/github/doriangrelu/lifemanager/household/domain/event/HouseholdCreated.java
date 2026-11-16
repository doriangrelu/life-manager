package io.github.doriangrelu.lifemanager.household.domain.event;

import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdName;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;

/** Un foyer a été créé ; son fondateur en est le premier membre. */
public record HouseholdCreated(HouseholdId householdId, HouseholdName name, MemberId founder, Instant occurredAt)
        implements DomainEvent {
}
