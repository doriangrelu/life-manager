package io.github.doriangrelu.lifemanager.household.application.port.out;

import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;

import java.util.Collection;
import java.util.Map;

/**
 * Annuaire local des noms d'affichage des membres.
 * Copie des données du domaine identité, tenue à jour par ses événements.
 */
public interface MemberDirectory {

    /** Noms connus pour ces membres (un membre inconnu est absent de la map). */
    Map<MemberId, String> displayNamesOf(Collection<MemberId> members);

    /** Enregistre ou remplace le nom d'affichage d'un membre. */
    void rename(MemberId member, String displayName);

    /** Efface la fiche (nom) d'un membre qui a supprimé son compte. */
    void forget(MemberId member);
}
