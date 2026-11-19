package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.ForgetHousehold;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.ForgetHouseholdCommand;
import io.github.doriangrelu.lifemanager.reminder.application.service.HouseholdEraser;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import jakarta.inject.Singleton;

/** Foyer supprimé : efface toutes ses données de ce domaine. Rejouable sans risque. */
@Singleton
class ForgetHouseholdUseCase implements ForgetHousehold {

    private final HouseholdEraser eraser;

    ForgetHouseholdUseCase(HouseholdEraser eraser) {
        this.eraser = eraser;
    }

    @Override
    public void forget(ForgetHouseholdCommand command) {
        eraser.erase(new HouseholdId(command.householdId()));
    }
}
