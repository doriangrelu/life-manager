package io.github.doriangrelu.lifemanager.shopping.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Élément inconnu dans l'historique de cette sous-liste. */
public class HistoryEntryNotFoundException extends NotFoundException {

    public HistoryEntryNotFoundException() {
        super("Article introuvable dans l'historique");
    }
}
