package io.github.doriangrelu.lifemanager.bills.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Échéance inconnue pour cette charge. */
public class OccurrenceNotFoundException extends NotFoundException {

    public OccurrenceNotFoundException() {
        super("Échéance introuvable");
    }
}
