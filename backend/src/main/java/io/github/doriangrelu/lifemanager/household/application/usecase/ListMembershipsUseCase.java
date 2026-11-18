package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.ListMemberships;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.MembershipView;
import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Liste chaque membre de chaque foyer. */
@Singleton
class ListMembershipsUseCase implements ListMemberships {

    private final HouseholdRepository households;

    ListMembershipsUseCase(HouseholdRepository households) {
        this.households = households;
    }

    @Override
    @Transactional(transactionManager = "household", readOnly = true)
    public List<MembershipView> listAll() {
        return households.findAll().stream()
                .flatMap(household -> household.members().stream()
                        .map(m -> new MembershipView(household.id().value(), m.memberId().value())))
                .toList();
    }
}
