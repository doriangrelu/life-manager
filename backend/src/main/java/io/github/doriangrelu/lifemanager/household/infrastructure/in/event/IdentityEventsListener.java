package io.github.doriangrelu.lifemanager.household.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.household.application.port.in.ForgetUser;
import io.github.doriangrelu.lifemanager.household.application.port.in.UpdateMemberProfile;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.ForgetUserCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.UpdateMemberProfileCommand;
import io.github.doriangrelu.lifemanager.identity.contract.event.UserDeletedV1;
import io.github.doriangrelu.lifemanager.identity.contract.event.UserProfileChangedV1;
import io.github.doriangrelu.lifemanager.identity.contract.event.UserRegisteredV1;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/**
 * Écoute les événements publics du domaine identité : annuaire local des membres, comptes supprimés.
 * Seul point de contact entre les deux domaines : demain, ces événements viendront d'un broker.
 */
@Singleton
class IdentityEventsListener {

    private final UpdateMemberProfile updateMemberProfile;
    private final ForgetUser forgetUser;

    IdentityEventsListener(UpdateMemberProfile updateMemberProfile, ForgetUser forgetUser) {
        this.updateMemberProfile = updateMemberProfile;
        this.forgetUser = forgetUser;
    }

    @EventListener
    void on(UserRegisteredV1 event) {
        updateMemberProfile.update(new UpdateMemberProfileCommand(event.userId(), event.displayName()));
    }

    @EventListener
    void on(UserProfileChangedV1 event) {
        updateMemberProfile.update(new UpdateMemberProfileCommand(event.userId(), event.displayName()));
    }

    @EventListener
    void on(UserDeletedV1 event) {
        forgetUser.forget(new ForgetUserCommand(event.userId()));
    }
}
