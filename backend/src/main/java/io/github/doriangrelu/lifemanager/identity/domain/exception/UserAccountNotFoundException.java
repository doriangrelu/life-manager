package io.github.doriangrelu.lifemanager.identity.domain.exception;

import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Aucun profil local pour cet utilisateur. */
public class UserAccountNotFoundException extends NotFoundException {

    public UserAccountNotFoundException(UserId userId) {
        super("Aucun profil pour l'utilisateur " + userId.value());
    }
}
