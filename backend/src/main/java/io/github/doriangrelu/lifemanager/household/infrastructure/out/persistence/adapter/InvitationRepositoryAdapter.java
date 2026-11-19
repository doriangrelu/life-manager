package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.household.application.port.out.InvitationRepository;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Invitation;
import io.github.doriangrelu.lifemanager.household.domain.exception.InvalidInvitationException;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationCode;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationStatus;
import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.mapper.InvitationMapper;
import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.repository.InvitationJdbcRepository;
import jakarta.inject.Singleton;

import java.util.Optional;

/** Implémente le stockage des invitations avec PostgreSQL (table invitation). */
@Singleton
class InvitationRepositoryAdapter implements InvitationRepository {

    private final InvitationJdbcRepository invitations;

    InvitationRepositoryAdapter(InvitationJdbcRepository invitations) {
        this.invitations = invitations;
    }

    @Override
    public Optional<Invitation> findByCode(InvitationCode code) {
        return invitations.findByCode(code.value()).map(InvitationMapper::toDomain);
    }

    @Override
    public Optional<Invitation> findPendingByHousehold(HouseholdId householdId) {
        return invitations.findByHouseholdIdAndStatus(householdId.value(), InvitationStatus.PENDING.name())
                .map(InvitationMapper::toDomain);
    }

    @Override
    public void save(Invitation invitation) {
        var entity = InvitationMapper.toEntity(invitation);
        if (!invitations.existsById(entity.id())) {
            invitations.save(entity);
            return;
        }
        // Une invitation ne fait que quitter l'état « en attente » : la mise à jour est conditionnelle
        // pour que deux personnes ne puissent pas utiliser le même code en même temps.
        if (invitation.status() != InvitationStatus.PENDING
                && invitations.closeIfPending(entity.id(), entity.status(), entity.acceptedBy()) == 0) {
            throw new InvalidInvitationException();
        }
    }

    @Override
    public void deleteByHousehold(HouseholdId householdId) {
        invitations.deleteByHouseholdId(householdId.value());
    }
}
