## Why

Retours d'usage de Dorian (nuit du 18 au 19 novembre) : petites gênes du quotidien, et la crainte d'une barre d'onglets surchargée quand les fonctions d'argent vont arriver (prévisionnel, comptes entre membres, projets). C'est le lot 2 de `docs/feuille-de-route.md`.

## What Changes

- **Navigation par espaces** : la barre du bas passe à quatre entrées stables, **Argent** (Budget, Charges, puis les futurs écrans d'argent), **Courses**, **Maison** (Véhicules, puis Documents, Rappels…) et **Foyer**. Dans un espace, des sous-onglets en pastilles ; sur grand écran, menu latéral groupé avec toutes les entrées visibles. Les adresses ne changent pas.
- **Budget** : bouton « Aujourd'hui » pour revenir au mois en cours depuis un autre mois.
- **Retour en haut** : après une saisie enchaînée de dépense, le formulaire repart en haut ; un changement d'écran repart en haut de page.
- **Noms des personnes** mis en forme partout (« dorian grelu » s'affiche « Dorian Grelu »).
- **Courses** : glisser-déposer pour ordonner les articles à acheter et les sous-listes, ordre partagé par tout le foyer.
- **Front** : composants communs rangés en atomes, molécules et organismes (Atomic Design), logique dupliquée regroupée.

## Capabilities

### New Capabilities

Aucune.

### Modified Capabilities

- `interface-pwa` : navigation par espaces ; retour en haut ; mise en forme des noms de personnes.
- `budget-mensuel` : retour au mois en cours.
- `liste-courses` : ordre des articles et des sous-listes choisi par le foyer.

## Impact

- `shopping` : colonne `position` sur articles et sous-listes (migration additive), deux points d'API de réordonnancement.
- PWA : navigation, budget, courses, formulaires ; nouvelle librairie `svelte-dnd-action`.
- Aucun autre domaine, aucune action sur le VPS.

## Hors périmètre

- Écrans des futurs lots (prévisionnel, documents…) : seuls leurs emplacements dans les espaces sont prévus.
- Ordre des articles déjà cochés (toujours du dernier coché au premier).
- Réordonner hors connexion.
