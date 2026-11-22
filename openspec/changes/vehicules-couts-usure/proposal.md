## Pourquoi

Demande de Dorian du 2026-11-22, à traiter après le lot 8 (Projets).

1. **Le coût d'une opération d'entretien n'arrive pas dans le budget.** Aujourd'hui, le coût saisi dans le carnet est une simple information : aucun lien n'existe entre le domaine `vehicle` et le budget. Une révision à 320 € n'apparaît donc pas dans les dépenses du mois, et il faut la saisir deux fois.
2. **Suivre les pièces d'usure** : la taille des pneus du véhicule, le jeu de pneus monté avec les kilomètres parcourus depuis la pose ; même chose pour les plaquettes de frein et les disques.

## Ce que je propose (à valider)

### A. Coût d'entretien → dépense du budget (v0.15.0)

- Une opération avec un coût crée **une dépense** dans le budget, comme une échéance de charge récurrente aujourd'hui (événement de contrat, boîte d'envoi, lien opération → dépense pour ne jamais la créer deux fois).
- Libellé : « Révision — Clio », catégorie **Transport** par défaut, payée par la personne qui saisit l'opération, pour tout le foyer (Commun).
- Modifier le coût ou la date de l'opération met la dépense à jour ; supprimer l'opération supprime la dépense.
- Sur le formulaire de l'opération, une case « Ajouter au budget » (cochée par défaut), pour ne pas compter deux fois ce qui a déjà été saisi à la main.

### B. Pièces d'usure (v0.16.0)

- Sur la fiche du véhicule : **kilométrage actuel** (mis à jour à la main, et automatiquement par une opération du carnet qui en porte un plus élevé).
- **Taille des pneus** du véhicule (ex. `205/55 R16 91V`), facultative.
- **Pièces montées** : pneus avant, pneus arrière, plaquettes avant, plaquettes arrière, disques avant, disques arrière. Pour chacune : date et kilométrage de pose, marque ou modèle facultatif. La fiche montre les **kilomètres parcourus** depuis la pose (kilométrage actuel − kilométrage à la pose).
- Remplacer une pièce garde l'historique (pose, dépose, kilomètres faits). Une opération du carnet de type « Pneus » peut proposer d'enregistrer le nouveau jeu.

## Questions pour Dorian

1. **Dépense d'entretien** : catégorie Transport par défaut avec possibilité d'en choisir une autre dans le formulaire, ça te va ? Payée par celui qui saisit, ou choix du payeur comme pour une dépense ?
2. **Opérations déjà saisies** avant cette version : faut-il créer leurs dépenses après coup (risque de doublon si tu les as déjà saisies à la main), ou seulement pour les nouvelles opérations (ma recommandation) ?
3. **Kilométrage actuel** : saisi à la main sur la fiche du véhicule et repris des opérations du carnet, ça te convient ?
4. **Pièces suivies** : pneus, plaquettes et disques, séparés avant / arrière. Faut-il d'autres pièces (batterie, courroie de distribution…) ou une alerte à un kilométrage donné (ex. plaquettes à 40 000 km) ?
5. **Découpage** : A d'abord (v0.15.0, petite version), puis B (v0.16.0) ?

## Impact (si validé)

- `vehicle` : événement de contrat `MaintenanceCostChangedV1` (création, modification, suppression), option « Ajouter au budget », puis kilométrage actuel, taille des pneus et pièces d'usure (nouvelles tables).
- `budget` : écoute de l'événement et lien opération → dépense, sur le modèle de `charge_expense`.
- PWA : case dans le formulaire d'opération ; puis bloc « Pièces d'usure » sur la fiche du véhicule.
