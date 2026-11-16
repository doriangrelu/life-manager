package io.github.doriangrelu.lifemanager.household.domain.aggregate;

import io.github.doriangrelu.lifemanager.household.domain.event.InvitationAccepted;
import io.github.doriangrelu.lifemanager.household.domain.exception.InvalidInvitationException;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationCode;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationStatus;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Invitation à rejoindre un foyer, matérialisée par un code.
 * Invariants : utilisable une seule fois, et pas après expiration.
 * Agrégat séparé de {@link Household} : les deux n'ont aucune règle en commun.
 */
public final class Invitation extends AggregateRoot<InvitationId> {

    /** Durée de validité d'un code. */
    public static final Duration VALIDITY = Duration.ofDays(7);

    private final HouseholdId householdId;
    private final InvitationCode code;
    private final Instant expiresAt;
    private InvitationStatus status;
    private MemberId acceptedBy;

    private Invitation(InvitationId id, HouseholdId householdId, InvitationCode code, Instant expiresAt,
                       InvitationStatus status, MemberId acceptedBy) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer de l'invitation");
        this.code = Require.notNull(code, "Le code d'invitation");
        this.expiresAt = Require.notNull(expiresAt, "La date d'expiration");
        this.status = Require.notNull(status, "Le statut de l'invitation");
        this.acceptedBy = acceptedBy;
    }

    /** Crée une invitation en attente, valable {@link #VALIDITY} à partir de maintenant. */
    public static Invitation issue(InvitationId id, HouseholdId householdId, InvitationCode code, Instant now) {
        return new Invitation(id, householdId, code, now.plus(VALIDITY), InvitationStatus.PENDING, null);
    }

    /** Recharge une invitation depuis le stockage (aucun événement produit). */
    @SuppressWarnings("checkstyle:ParameterNumber") // un paramètre par colonne stockée
    public static Invitation restore(InvitationId id, HouseholdId householdId, InvitationCode code, Instant expiresAt,
                                     InvitationStatus status, MemberId acceptedBy) {
        return new Invitation(id, householdId, code, expiresAt, status, acceptedBy);
    }

    /** Vrai tant que le code peut encore servir. */
    public boolean isUsable(Instant now) {
        return status == InvitationStatus.PENDING && now.isBefore(expiresAt);
    }

    /** Consomme le code au profit de {@code member} ; refusé s'il est expiré ou déjà utilisé. */
    public void accept(MemberId member, Instant now) {
        if (!isUsable(now)) {
            throw new InvalidInvitationException();
        }
        status = InvitationStatus.ACCEPTED;
        acceptedBy = Require.notNull(member, "Le membre");
        recordEvent(new InvitationAccepted(id(), householdId, member, now));
    }

    /** Annule un code encore en attente (quand un nouveau code le remplace). */
    public void revoke() {
        if (status == InvitationStatus.PENDING) {
            status = InvitationStatus.REVOKED;
        }
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public InvitationCode code() {
        return code;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public InvitationStatus status() {
        return status;
    }

    public Optional<MemberId> acceptedBy() {
        return Optional.ofNullable(acceptedBy);
    }
}
