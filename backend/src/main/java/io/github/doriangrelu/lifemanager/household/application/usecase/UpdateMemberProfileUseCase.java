package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.UpdateMemberProfile;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.UpdateMemberProfileCommand;
import io.github.doriangrelu.lifemanager.household.application.port.out.MemberDirectory;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Recopie le nom d'affichage d'un utilisateur dans l'annuaire local du domaine foyer. */
@Singleton
class UpdateMemberProfileUseCase implements UpdateMemberProfile {

    private final MemberDirectory directory;

    UpdateMemberProfileUseCase(MemberDirectory directory) {
        this.directory = directory;
    }

    @Override
    @Transactional(transactionManager = "household")
    public void update(UpdateMemberProfileCommand command) {
        directory.rename(new MemberId(command.memberId()), command.displayName());
    }
}
