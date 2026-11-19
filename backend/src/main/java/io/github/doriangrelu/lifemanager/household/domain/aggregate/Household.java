package io.github.doriangrelu.lifemanager.household.domain.aggregate;

import io.github.doriangrelu.lifemanager.household.domain.entity.Membership;
import io.github.doriangrelu.lifemanager.household.domain.event.HouseholdCreated;
import io.github.doriangrelu.lifemanager.household.domain.event.HouseholdDeleted;
import io.github.doriangrelu.lifemanager.household.domain.event.MemberJoined;
import io.github.doriangrelu.lifemanager.household.domain.event.MemberLeft;
import io.github.doriangrelu.lifemanager.household.domain.exception.AlreadyMemberException;
import io.github.doriangrelu.lifemanager.household.domain.exception.HouseholdNameMismatchException;
import io.github.doriangrelu.lifemanager.household.domain.exception.MemberNotFoundException;
import io.github.doriangrelu.lifemanager.household.domain.exception.NotOwnerException;
import io.github.doriangrelu.lifemanager.household.domain.exception.OwnerCannotLeaveException;
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
 * Invariants : un nom valide, au moins un membre, jamais deux fois le même membre,
 * un seul propriétaire qui est membre (il ne peut ni partir ni être retiré).
 */
public final class Household extends AggregateRoot<HouseholdId> {

    private HouseholdName name;
    private final Instant createdAt;
    private MemberId owner;
    // Suivi des rentrées (bilan) : réglage du foyer, désactivé par défaut, changé par le propriétaire.
    private boolean incomeTracking;
    // Ordre d'adhésion conservé pour l'affichage.
    private final Map<MemberId, Membership> members = new LinkedHashMap<>();

    private Household(HouseholdId id, HouseholdName name, Instant createdAt, MemberId owner,
                      Collection<Membership> members) {
        super(id);
        this.name = Require.notNull(name, "Le nom du foyer");
        this.createdAt = Require.notNull(createdAt, "La date de création");
        if (members.isEmpty()) {
            throw new InvalidValueException("Un foyer a au moins un membre");
        }
        members.forEach(membership -> this.members.put(membership.memberId(), membership));
        this.owner = Require.notNull(owner, "Le propriétaire du foyer");
        if (!isMember(owner)) {
            throw new InvalidValueException("Le propriétaire est membre du foyer");
        }
    }

    /** Crée un foyer dont le fondateur est le premier membre et le propriétaire. */
    public static Household create(HouseholdId id, HouseholdName name, MemberId founder, Instant now) {
        var household = new Household(id, name, now, founder, List.of(new Membership(founder, now)));
        household.recordEvent(new HouseholdCreated(id, name, founder, now));
        return household;
    }

    /** Recharge un foyer existant depuis le stockage (aucun événement produit). */
    public static Household restore(HouseholdId id, HouseholdName name, Instant createdAt, MemberId owner,
                                    Collection<Membership> members) {
        return new Household(id, name, createdAt, owner, members);
    }

    /** Recharge un foyer existant avec ses réglages (aucun événement produit). */
    @SuppressWarnings("checkstyle:ParameterNumber") // un paramètre par donnée stockée
    public static Household restore(HouseholdId id, HouseholdName name, Instant createdAt, MemberId owner,
                                    Collection<Membership> members, boolean incomeTracking) {
        var household = new Household(id, name, createdAt, owner, members);
        household.incomeTracking = incomeTracking;
        return household;
    }

    /** Le propriétaire active ou désactive le suivi des rentrées (bilan) ; aucune donnée n'est supprimée. */
    public void trackIncome(MemberId by, boolean enabled) {
        requireOwner(by);
        incomeTracking = enabled;
    }

    public boolean incomeTracking() {
        return incomeTracking;
    }

    /** Ajoute un membre au foyer ; refusé s'il en fait déjà partie. */
    public void addMember(MemberId member, Instant now) {
        if (isMember(member)) {
            throw new AlreadyMemberException();
        }
        members.put(member, new Membership(member, now));
        recordEvent(new MemberJoined(id(), member, now));
    }

    /** @throws NotOwnerException si {@code member} n'est pas le propriétaire */
    public void requireOwner(MemberId member) {
        if (!isOwner(member)) {
            throw new NotOwnerException();
        }
    }

    /** Le propriétaire passe la main à un autre membre, qui devient propriétaire. */
    public void transferOwnership(MemberId by, MemberId newOwner) {
        requireOwner(by);
        if (!isMember(newOwner)) {
            throw new MemberNotFoundException();
        }
        owner = newOwner;
    }

    /** Le propriétaire retire un autre membre ; le membre perd l'accès au foyer partout. */
    public void removeMember(MemberId by, MemberId member, Instant now) {
        requireOwner(by);
        if (!isMember(member)) {
            throw new MemberNotFoundException();
        }
        detach(member, now);
    }

    /** Un membre quitte le foyer de lui-même ; le propriétaire doit d'abord transmettre ou supprimer. */
    public void leave(MemberId member, Instant now) {
        if (!isMember(member)) {
            throw new MemberNotFoundException();
        }
        detach(member, now);
    }

    public void rename(MemberId by, HouseholdName newName) {
        requireOwner(by);
        name = Require.notNull(newName, "Le nom du foyer");
    }

    /**
     * Le propriétaire supprime le foyer en retapant son nom : toutes ses données, dans tous les domaines, disparaîtront.
     *
     * @throws HouseholdNameMismatchException si le nom retapé n'est pas celui du foyer
     */
    public void delete(MemberId by, HouseholdName confirmation, Instant now) {
        requireOwner(by);
        if (!name.equals(confirmation)) {
            throw new HouseholdNameMismatchException();
        }
        recordEvent(new HouseholdDeleted(id(), now));
    }

    private void detach(MemberId member, Instant now) {
        if (isOwner(member)) {
            throw new OwnerCannotLeaveException();
        }
        members.remove(member);
        recordEvent(new MemberLeft(id(), member, owner, now));
    }

    public boolean isMember(MemberId member) {
        return members.containsKey(member);
    }

    public boolean isOwner(MemberId member) {
        return owner.equals(member);
    }

    public HouseholdName name() {
        return name;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public MemberId owner() {
        return owner;
    }

    /** Membres par ordre d'adhésion. */
    public List<Membership> members() {
        return List.copyOf(members.values());
    }
}
