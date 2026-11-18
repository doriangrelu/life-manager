package io.github.doriangrelu.lifemanager.bills.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;

/** Une échéance payée ou sautée ne change plus. */
public class OccurrenceAlreadySettledException extends ConflictException {

    public OccurrenceAlreadySettledException() {
        super("Cette échéance est déjà payée ou sautée");
    }
}
