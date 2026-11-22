package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.ForgetProjectCommand;

/** Oublie un projet supprimé ; ses dépenses n'ont plus de projet. */
public interface ForgetProject {

    void forget(ForgetProjectCommand command);
}
