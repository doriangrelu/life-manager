package io.github.doriangrelu.lifemanager.kernel.application.port.out;

import java.util.Map;
import java.util.UUID;

/**
 * Part d'un domaine dans l'export des données personnelles : ce que l'utilisateur y a lui-même saisi.
 * Valeurs simples uniquement (texte, nombres, booléens, listes et tableaux de ces valeurs), sans secret
 * ni identifiant technique autre que le lien vers le foyer, sans les données des autres membres.
 */
public interface PersonalDataSource {

    /** Nom de la section dans le fichier (« budget », « courses »…). */
    String section();

    /** Ordre d'apparition dans le fichier (le plus petit d'abord). */
    int order();

    /** Données saisies par ce membre (son {@code sub} Keycloak). */
    Map<String, Object> exportFor(UUID memberId);
}
