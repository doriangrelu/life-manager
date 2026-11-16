package io.github.doriangrelu.lifemanager.household.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;

/** L'utilisateur fait déjà partie de ce foyer. */
public class AlreadyMemberException extends ConflictException {

    public AlreadyMemberException() {
        super("Vous êtes déjà membre de ce foyer");
    }
}
