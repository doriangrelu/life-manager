package io.github.doriangrelu.lifemanager.household.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;

/** L'utilisateur appartient déjà au nombre maximal de foyers. */
public class MembershipLimitReachedException extends ConflictException {

    public MembershipLimitReachedException(int limit) {
        super("Vous ne pouvez pas appartenir à plus de " + limit + " foyers");
    }
}
