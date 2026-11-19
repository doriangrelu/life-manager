package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.ForgetHousehold;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ForgetHouseholdCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.out.InvoiceStorage;
import io.github.doriangrelu.lifemanager.bills.application.service.HouseholdEraser;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.kernel.domain.file.StorageKey;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;

/** Foyer supprimé : efface ses données en base, puis ses fichiers (jamais l'inverse). Rejouable sans risque. */
@Singleton
class ForgetHouseholdUseCase implements ForgetHousehold {

    private static final Logger LOG = LoggerFactory.getLogger(ForgetHouseholdUseCase.class);

    private final HouseholdEraser eraser;
    private final InvoiceStorage storage;

    ForgetHouseholdUseCase(HouseholdEraser eraser, InvoiceStorage storage) {
        this.eraser = eraser;
        this.storage = storage;
    }

    @Override
    public void forget(ForgetHouseholdCommand command) {
        for (var key : eraser.erase(new HouseholdId(command.householdId()))) {
            try {
                storage.delete(new StorageKey(key));
            } catch (UncheckedIOException e) {
                // Fichier orphelin : sans gravité pour l'utilisateur (déjà invisible), à nettoyer à la main.
                LOG.warn("Fichier {} du foyer supprimé non effacé", key, e);
            }
        }
    }
}
