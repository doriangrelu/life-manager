package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.adapter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

/** Calcul d'un nouvel ordre : ceux demandés d'abord, puis les autres dans leur ordre actuel. */
final class Ordering {

    private Ordering() {
    }

    /**
     * @param current   identifiants dans l'ordre actuel
     * @param requested identifiants dans l'ordre voulu (tous présents dans {@code current})
     * @return l'ordre complet à enregistrer
     */
    static List<UUID> merge(List<UUID> current, List<UUID> requested) {
        var ordered = new LinkedHashSet<>(requested);
        ordered.addAll(current);
        return new ArrayList<>(ordered);
    }
}
