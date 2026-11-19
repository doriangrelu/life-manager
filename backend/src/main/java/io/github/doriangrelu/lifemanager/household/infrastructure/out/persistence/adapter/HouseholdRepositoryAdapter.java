package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity.HouseholdEntity;
import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.mapper.HouseholdMapper;
import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.repository.HouseholdJdbcRepository;
import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.repository.MembershipJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Implémente le stockage des foyers avec PostgreSQL (tables household et membership). */
@Singleton
class HouseholdRepositoryAdapter implements HouseholdRepository {

    private final HouseholdJdbcRepository households;
    private final MembershipJdbcRepository memberships;

    HouseholdRepositoryAdapter(HouseholdJdbcRepository households, MembershipJdbcRepository memberships) {
        this.households = households;
        this.memberships = memberships;
    }

    @Override
    public Optional<Household> findById(HouseholdId id) {
        return households.findById(id.value()).map(this::withMembers);
    }

    @Override
    public List<Household> findAllByMember(MemberId member) {
        return households.findAllByMember(member.value()).stream().map(this::withMembers).toList();
    }

    @Override
    public List<Household> findAll() {
        return households.findAll().stream().map(this::withMembers).toList();
    }

    @Override
    public int countByMember(MemberId member) {
        return Math.toIntExact(memberships.countByMemberId(member.value()));
    }

    @Override
    public void save(Household household) {
        var id = household.id().value();
        if (households.existsById(id)) {
            households.update(HouseholdMapper.toEntity(household));
        } else {
            households.save(HouseholdMapper.toEntity(household));
        }
        var current = household.members().stream().map(membership -> membership.memberId().value())
                .collect(Collectors.toSet());
        // Membres partis ou retirés : leur appartenance disparaît.
        memberships.findByHouseholdIdOrderByJoinedAt(id).stream()
                .filter(row -> !current.contains(row.memberId()))
                .forEach(memberships::delete);
        // Nouveaux membres : ajoutés.
        household.members().stream()
                .filter(membership -> !memberships.existsByHouseholdIdAndMemberId(id, membership.memberId().value()))
                .forEach(membership -> memberships.save(HouseholdMapper.toNewEntity(household.id(), membership)));
    }

    @Override
    public void delete(Household household) {
        var id = household.id().value();
        memberships.deleteAll(memberships.findByHouseholdIdOrderByJoinedAt(id));
        households.deleteById(id);
    }

    private Household withMembers(HouseholdEntity household) {
        return HouseholdMapper.toDomain(household, memberships.findByHouseholdIdOrderByJoinedAt(household.id()));
    }
}
