package io.github.doriangrelu.lifemanager.household.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** La personne visée n'est pas membre de ce foyer. */
public class MemberNotFoundException extends NotFoundException {

    public MemberNotFoundException() {
        super("Membre introuvable dans ce foyer");
    }
}
