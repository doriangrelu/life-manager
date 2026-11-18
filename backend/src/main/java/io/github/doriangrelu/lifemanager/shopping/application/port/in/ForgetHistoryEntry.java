package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ForgetHistoryEntryCommand;

/** Retire un article de l'historique d'une sous-liste. */
public interface ForgetHistoryEntry {

    void forget(ForgetHistoryEntryCommand command);
}
