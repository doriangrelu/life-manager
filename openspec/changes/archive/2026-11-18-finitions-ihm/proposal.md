## Why

Retour de Dorian (2026-11-18) : l'interface est « très bien mais légèrement brute ». Rien n'indique qu'un écran charge ou qu'un enregistrement est en cours, les éléments ne réagissent pas au survol, et le montant de la saisie de dépense n'est pas centré.

## What Changes

- **Chargements** : formes grises animées (« squelettes ») à la place des listes et des synthèses pendant leur premier chargement (Budget, Courses, Véhicules, carnet, Foyer, historique, démarrage). Plus de « Aucun véhicule » ou « Aucune sous-liste » affiché à tort avant l'arrivée des données. Au changement de mois, l'ancien mois reste affiché, estompé.
- **Enregistrements** : un bouton en attente affiche un petit rond qui tourne à la place de son icône.
- **Survol et appui** : réaction visuelle des boutons, lignes de liste et cartes au survol de la souris (jamais sur écran tactile), léger enfoncement à l'appui ; animations réduites si le système le demande.
- **Montant** : le montant et « € » sont centrés ensemble dans la fenêtre de saisie, et le champ prend la main à l'ouverture (clavier numérique directement sur mobile).
- **Détails** : boutons dont le texte n'est jamais coupé sur deux lignes (carte d'invitation).

## Capabilities

### Modified Capabilities

- `interface-pwa` : retours visuels de chargement, d'attente et de survol.

## Impact

- PWA uniquement : `app.css`, nouveau composant `ui/Skeleton.svelte`, `ui/AmountInput.svelte`, `ui/Modal.svelte`, écrans concernés.

## Hors périmètre

- Mode hors connexion, indicateur de connexion perdue.
- Refonte de la charte graphique.
