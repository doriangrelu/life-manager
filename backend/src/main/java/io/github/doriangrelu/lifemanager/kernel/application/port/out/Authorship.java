package io.github.doriangrelu.lifemanager.kernel.application.port.out;

import java.util.UUID;

/**
 * Qui a créé un objet du foyer (catégorie, récurrent, véhicule, liste, rappel) : sert seulement à l'export
 * des données personnelles. Une table par domaine, effacée avec le foyer.
 */
public interface Authorship {

    /** Note (ou remplace) l'auteur de l'objet. */
    void record(UUID objectId, UUID householdId, UUID memberId);
}
