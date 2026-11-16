package io.github.doriangrelu.lifemanager.household.domain.entity;

import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.entity.Entity;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Instant;

/**
 * Appartenance d'un utilisateur à un foyer. N'existe qu'à l'intérieur de l'agrégat {@code Household} :
 * au sein d'un foyer, un membre est identifié par son {@link MemberId}.
 */
public final class Membership extends Entity<MemberId> {

    private final Instant joinedAt;

    public Membership(MemberId memberId, Instant joinedAt) {
        super(memberId);
        this.joinedAt = Require.notNull(joinedAt, "La date d'adhésion");
    }

    public MemberId memberId() {
        return id();
    }

    public Instant joinedAt() {
        return joinedAt;
    }
}
