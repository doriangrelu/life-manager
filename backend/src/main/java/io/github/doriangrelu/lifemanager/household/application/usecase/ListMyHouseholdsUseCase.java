package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.ListMyHouseholds;
import io.github.doriangrelu.lifemanager.household.application.port.in.query.ListMyHouseholdsQuery;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.HouseholdSummary;
import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Liste les foyers du demandeur, du plus ancien au plus récent. */
@Singleton
class ListMyHouseholdsUseCase implements ListMyHouseholds {

    private final HouseholdRepository households;

    ListMyHouseholdsUseCase(HouseholdRepository households) {
        this.households = households;
    }

    @Override
    @Transactional(transactionManager = "household", readOnly = true)
    public List<HouseholdSummary> list(ListMyHouseholdsQuery query) {
        return households.findAllByMember(MemberId.of(query.requesterId())).stream()
                .map(HouseholdSummary::from)
                .toList();
    }
}
