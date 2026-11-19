package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.ForgetHousehold;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.ForgetHouseholdCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.AttachmentStorage;
import io.github.doriangrelu.lifemanager.vehicle.application.service.HouseholdEraser;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
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
    private final AttachmentStorage storage;

    ForgetHouseholdUseCase(HouseholdEraser eraser, AttachmentStorage storage) {
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
