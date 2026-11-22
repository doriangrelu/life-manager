## Pourquoi

Demande de Dorian du 2026-11-22, à traiter après le lot 8 (Projets).

1. **Le coût d'une opération d'entretien n'arrive pas dans le budget.** Le coût saisi dans le carnet n'est qu'une information : aucun lien n'existe entre le domaine `vehicle` et le budget. Une révision à 320 € n'apparaît pas dans les dépenses du mois, il faut la saisir deux fois.
2. **Suivre les pièces d'usure** : la taille des pneus, le jeu monté avec les kilomètres parcourus depuis la pose ; même chose pour les plaquettes et les disques.

Consigne de Dorian : des fonctionnalités **simples à utiliser**, sans écran de plus que nécessaire.

## Réponses de Dorian (22/11)

1. Catégorie **Transport** par défaut, modifiable ; le payeur se choisit comme pour une dépense (soi-même par défaut).
2. Opérations déjà saisies : « oui pourquoi pas ». Pour ne jamais créer de doublon, rien n'est créé d'office : chaque ancienne opération avec un coût montre un bouton **« Ajouter au budget »**.
3. Kilométrage actuel saisi à la main et repris du carnet : oui.
4. Autres pièces et alerte au kilométrage : oui, **attention aux moteurs à chaîne de distribution** (pas de courroie à changer).
5. Découpage : A (v0.15.0) puis B (v0.16.0).

## Ce qui change

### A. Coût d'entretien → dépense du budget (v0.15.0)

- Formulaire d'opération : dès qu'un coût est saisi, une case **« Ajouter au budget »** (cochée), avec la catégorie (Transport par défaut) et qui a payé (soi-même par défaut).
- La dépense créée a pour libellé « révision · clio » (type et véhicule), pour tout le foyer, à la date de l'opération.
- Carnet : une opération envoyée au budget porte la mention « Dans le budget » ; une opération avec un coût qui ne l'est pas propose « Ajouter au budget ».
- Supprimer l'opération supprime sa dépense. Supprimer le véhicule (vendu) garde les dépenses passées.
- La dépense reste une dépense comme les autres : modifiable ou supprimable dans le Budget.

### B. Kilométrage et pièces d'usure (v0.16.0)

- Fiche du véhicule : **kilométrage actuel** (saisi à la main, relevé automatiquement par une opération du carnet qui en porte un plus élevé) et **taille des pneus** (facultative).
- Bloc **« Pièces d'usure »** sur la fiche : pneus avant, pneus arrière, plaquettes avant, plaquettes arrière, disques avant, disques arrière, batterie, courroie de distribution. On n'ajoute que les pièces qu'on veut suivre (la courroie porte la mention « inutile si le moteur a une chaîne »).
- Pour chaque pièce montée : date et kilométrage de pose, marque ou modèle facultatif, **à changer tous les … km** facultatif. La fiche montre les **km parcourus** depuis la pose et, si un intervalle est saisi, « à prévoir » à 90 % puis « à changer ».
- **Changer** une pièce enregistre la nouvelle et garde l'ancienne dans l'historique (km faits).

## Impact

- `vehicle` : nouvel agrégat `MaintenanceExpense` (lien opération → budget), événements de contrat `MaintenanceExpenseSentV1` et `MaintenanceExpenseWithdrawnV1`, route `POST …/maintenance/{id}/budget` ; puis kilométrage, taille des pneus, agrégat `WearPart` et routes `…/parts`.
- `budget` : écoute des deux événements ; la dépense est créée par le même mécanisme que les échéances de charges (une opération ne produit jamais deux dépenses).
- PWA : formulaire et carnet du véhicule ; puis fiche du véhicule et bloc « Pièces d'usure ».
- Migrations `vehicle/V6` (v0.15.0) et `vehicle/V7` (v0.16.0), aucune dans `budget`.
