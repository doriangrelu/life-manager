package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ForgetHouseholdMember;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ForgetHouseholdMemberCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.HouseholdData;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Un membre a quitté le foyer : il n'y a plus accès dans ce domaine. Sans effet si c'est déjà fait. */
@Singleton
class ForgetHouseholdMemberUseCase implements ForgetHouseholdMember {

    private final HouseholdData data;

    ForgetHouseholdMemberUseCase(HouseholdData data) {
        this.data = data;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public void forget(ForgetHouseholdMemberCommand command) {
        var household = new HouseholdId(command.householdId());
        var member = new MemberId(command.memberId());
        data.forgetMember(household, member);
    }
}
