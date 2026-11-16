package io.github.doriangrelu.lifemanager.household.domain.aggregate;

import io.github.doriangrelu.lifemanager.household.domain.entity.Membership;
import io.github.doriangrelu.lifemanager.household.domain.event.HouseholdCreated;
import io.github.doriangrelu.lifemanager.household.domain.event.MemberJoined;
import io.github.doriangrelu.lifemanager.household.domain.exception.AlreadyMemberException;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdName;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Foyer : un groupe de personnes qui partagent leurs données (budget, courses…).
 * Invariants : un nom valide, au moins un membre, jamais deux fois le même membre.
 */
public final class Household extends AggregateRoot<HouseholdId> {

    private final HouseholdName name;
    private final Instant createdAt;
    // Ordre d'adhésion conservé pour l'affichage.
    private final Map<MemberId, Membership> members = new LinkedHashMap<>();

    private Household(HouseholdId id, HouseholdName name, Instant createdAt, Collection<Membership> members) {
        super(id);
        this.name = Require.notNull(name, "Le nom du foyer");
        this.createdAt = Require.notNull(createdAt, "La date de création");
        if (members.isEmpty()) {
            throw new InvalidValueException("Un foyer a au moins un membre");
        }
        members.forEach(membership -> this.members.put(membership.memberId(), membership));
    }

    /** Crée un foyer dont le fondateur est le premier membre. */
    public static Household create(HouseholdId id, HouseholdName name, MemberId founder, Instant now) {
        var household = new Household(id, name, now, List.of(new Membership(founder, now)));
        household.recordEvent(new HouseholdCreated(id, name, founder, now));
        return household;
    }

    /** Recharge un foyer existant depuis le stockage (aucun événement produit). */
    public static Household restore(HouseholdId id, HouseholdName name, Instant createdAt,
                                    Collection<Membership> members) {
        return new Household(id, name, createdAt, members);
    }

    /** Ajoute un membre au foyer ; refusé s'il en fait déjà partie. */
    public void addMember(MemberId member, Instant now) {
        if (isMember(member)) {
            throw new AlreadyMemberException();
        }
        members.put(member, new Membership(member, now));
        recordEvent(new MemberJoined(id(), member, now));
    }

    public boolean isMember(MemberId member) {
        return members.containsKey(member);
    }

    public HouseholdName name() {
        return name;
    }

    public Instant createdAt() {
        return createdAt;
    }

    /** Membres par ordre d'adhésion. */
    public List<Membership> members() {
        return List.copyOf(members.values());
    }
}
