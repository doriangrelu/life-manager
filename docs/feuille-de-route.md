# Feuille de route

> **Proposition à valider par Dorian** (2026-11-18). Chaque lot deviendra un change OpenSpec (proposé, relu, appliqué, archivé), une version `vX.Y.0` et un déploiement.

## Où on en est

Le **MVP de la vision est livré et en production** (v0.1.1 sur https://maison.dorian-grelu.fr) :

| Domaine | Livré |
|---|---|
| Socle | comptes Keycloak, foyers, invitation par code |
| Budget | saisie rapide, catégories libres, vue mensuelle par catégorie et par personne |
| Véhicules | fiches, carnet d'entretien, pièces jointes, échéances CT et révision calculées |
| Courses | listes partagées en temps réel, sous-listes, historique et remise en liste |
| Rappels | échéances des véhicules, notifications de l'appareil (application ouverte) |
| PWA | navigation, écran conservé au rechargement, mode sombre |

## Ordre proposé

### Lot 0 — Finitions de la mise en production ✅ (terminé le 2026-11-18, v0.2.0 à v0.2.5)

- ✅ Thème de connexion en production.
- ✅ Pièces jointes dans la sauvegarde nocturne, archive vérifiée.
- ✅ Messages d'erreur clairs (serveur injoignable, panne serveur) et retour de connexion sans erreur.
- ✅ Retour arrière vérifié entre deux vraies versions.
- ✅ Parcours d'usage sur l'exécutable natif en CI.
- En plus, d'après les retours de Dorian : noms normalisés, chargements, animations, survol, mise à jour automatique de la PWA.
- Reste : test de restauration complète sur une machine vierge (voir dette technique).

### Lot 1 — Factures et abonnements ✅ (livré le 2026-11-18, v0.3.0, en recette)

Électricité, internet, assurances, téléphone… : ce qui revient chaque mois.

- ✅ Onglet « Charges » : charges récurrentes (montant fixe ou variable, fréquence, catégorie, payé par / pour).
- ✅ Montant fixe : la dépense arrive seule dans le budget le jour de l'échéance. Montant variable : échéance « à confirmer » avec le montant de la facture.
- ✅ Rappel 7 jours avant l'échéance ; rappel tant qu'un montant reste à confirmer.
- ✅ Factures jointes à chaque échéance (socle des pièces jointes partagé avec le carnet).
- ✅ Nouveau domaine `bills`.

### Lot 2 — Rappels libres (V2)

Aujourd'hui les rappels ne viennent que des véhicules.

- Rappels saisis à la main : date, récurrence (annuelle, mensuelle), note. Exemples : déclaration d'impôts, ramonage, renouvellement de mutuelle.
- Même affichage et mêmes notifications que les rappels de véhicules.
- Prépare le lot 4 (dates d'expiration des documents).

### Lot 3 — Crédits et budget prévisionnel (V2)

- Crédits en cours : capital, taux, mensualité, échéancier, capital restant dû.
- Budget prévisionnel du mois : revenus + charges récurrentes (lot 1) + mensualités → **reste à vivre**.
- Comparaison prévu / réel par catégorie.

Dépend du lot 1.

### Lot 4 — Documents et démarches (V2, données les plus sensibles)

- Coffre-fort de documents du foyer (carte grise, contrats, actes, diplômes…), classés par type et par personne.
- Dates d'expiration (CNI, passeport, permis) → rappels (lot 2).
- **À trancher avant** : chiffrement des fichiers au repos sur le VPS, qui peut y accéder dans le foyer.

### Lot 5 — Notifications push (amélioration des rappels)

Aujourd'hui, une notification n'apparaît que si l'application est ouverte.

- Web Push : notification sur le téléphone même application fermée (Android, et iPhone si la PWA est installée sur l'écran d'accueil).
- Choix par membre de ce qu'il reçoit.

### Lot 6 — Courses hors connexion

- La liste reste consultable et cochable dans un magasin sans réseau ; synchronisation au retour du réseau.

### Plus tard / à reconsidérer

- Import CSV des relevés bancaires (la vision exclut l'agrégation automatique).
- Statistiques sur l'année (évolution des dépenses, coût réel de chaque véhicule).

## Dette technique à traiter au fil de l'eau

| Sujet | Risque aujourd'hui | Quand |
|---|---|---|
| Événements entre domaines sans « outbox » | un arrêt brutal pendant une publication peut désynchroniser une copie locale (membres, échéances) | avant le lot 1, qui ajoute des événements |
| HTTPS sortant depuis l'exécutable natif | échoue (`decrypt_error`) ; contourné par le réseau interne | dès qu'un appel externe sera nécessaire (Web Push, lot 5) |
| Test de restauration complète | jamais fait sur une machine vierge | dès qu'un VPS de test est disponible |

## Questions pour Dorian

1. L'ordre des lots te convient-il ? Qu'est-ce qui te manque le plus au quotidien ?
2. Des besoins absents de cette liste ?
3. Ta femme utilise-t-elle déjà l'application ? Ses retours peuvent changer les priorités.
