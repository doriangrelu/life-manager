package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.ForgetUserCommand;

/** Un utilisateur a supprimé son compte : ses foyers sont supprimés, transmis ou quittés, sa fiche effacée. */
public interface ForgetUser {

    void forget(ForgetUserCommand command);
}
