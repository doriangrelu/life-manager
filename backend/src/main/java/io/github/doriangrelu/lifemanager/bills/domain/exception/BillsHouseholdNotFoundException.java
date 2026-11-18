package io.github.doriangrelu.lifemanager.bills.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Foyer inconnu, ou dont le demandeur n'est pas membre. */
public class BillsHouseholdNotFoundException extends NotFoundException {

    public BillsHouseholdNotFoundException() {
        super("Foyer introuvable");
    }
}
