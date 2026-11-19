package io.github.doriangrelu.lifemanager.household.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ForbiddenException;

/** Action réservée au propriétaire du foyer. */
public class NotOwnerException extends ForbiddenException {

    public NotOwnerException() {
        super("Seul le propriétaire du foyer peut faire cela");
    }
}
