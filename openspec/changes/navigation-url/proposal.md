## Why

Retour de Dorian : un rechargement de la page (F5) ramène toujours sur l'écran Budget du mois courant, quel que soit l'écran affiché. La page en cours n'est écrite nulle part dans l'adresse. Conséquence voisine : sur Android, le bouton « Retour » quitte l'application au lieu de revenir à l'écran précédent.

## What Changes

- **Adresse = écran affiché** : la section (Budget, Courses, Véhicules, Foyer), le mois consulté dans Budget et le véhicule ouvert sont écrits dans l'adresse de la page (partie après `#`).
- **Rechargement** : F5, ou la réouverture de la PWA à cette adresse, réaffiche le même écran. Si l'élément n'existe plus (véhicule supprimé), la PWA affiche la liste de la section.
- **Précédent / suivant** : les boutons du navigateur et le bouton « Retour » d'Android naviguent entre les écrans visités.
- Ce qui n'est **pas** conservé : une fenêtre modale ouverte (saisie en cours), la position de défilement. Le foyer sélectionné et la sous-liste de courses restent mémorisés sur l'appareil, comme aujourd'hui.

## Capabilities

### New Capabilities

_Aucune._

### Modified Capabilities

- `interface-pwa` : l'écran affiché est conservé au rechargement et suit l'historique du navigateur.

## Impact

- PWA uniquement : un petit module de routage (`lib/route.ts`) qui lit et écrit l'adresse, branché sur `HouseholdTabs`, `BudgetView` (mois) et `VehiclesView` (véhicule ouvert).
- Aucun changement du backend ni de la configuration du serveur (adresse avec `#`, voir le design).

## Hors périmètre

- Liens partageables entre membres (le foyer n'est pas dans l'adresse).
- Conservation des modales et des saisies en cours.
