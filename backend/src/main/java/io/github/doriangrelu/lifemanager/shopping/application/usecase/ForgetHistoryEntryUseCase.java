package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ForgetHistoryEntry;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ForgetHistoryEntryCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.PurchaseHistoryRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.PurchaseHistoryAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Retire un article de l'historique d'une sous-liste. */
@Singleton
class ForgetHistoryEntryUseCase implements ForgetHistoryEntry {

    private final PurchaseHistoryAccess access;
    private final PurchaseHistoryRepository history;

    ForgetHistoryEntryUseCase(PurchaseHistoryAccess access, PurchaseHistoryRepository history) {
        this.access = access;
        this.history = history;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public void forget(ForgetHistoryEntryCommand command) {
        history.delete(access.entryForMember(command.householdId(), command.listId(), command.entryId(),
                command.requesterId()));
    }
}
