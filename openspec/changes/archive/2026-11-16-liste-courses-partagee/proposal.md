## Why

Partager une liste de courses entre les deux membres du foyer est le troisième objectif de la vision (`docs/vision.md`). Aujourd'hui la liste vit sur un papier ou dans une messagerie : l'un ajoute, l'autre ne le voit pas au magasin.

## What Changes

- Nouveau domaine **shopping**, une liste unique par foyer, dans son propre schéma de base.
- **Articles** : ajout rapide d'un libellé libre (« 2 l de lait »), cochage et décochage d'un clic, suppression, et « vider les articles cochés » en fin de courses. Tout membre du foyer peut tout faire.
- **Synchronisation** entre membres : la PWA rafraîchit la liste toutes les 5 secondes tant que l'écran est visible, et dès qu'il redevient visible.
- Le domaine **shopping** connaît les membres de chaque foyer grâce aux événements de contrat du foyer (`HouseholdCreatedV1`, `MemberJoinedV1`).
- **PWA** : onglet « Courses » pensé pour une main : champ d'ajout en haut, grandes cases à cocher, articles cochés en bas de la liste.

## Capabilities

### New Capabilities

- `liste-courses` : liste d'articles partagée par les membres d'un foyer, ajout, cochage, suppression et synchronisation.

### Modified Capabilities

_Aucune._

## Impact

- Backend : nouveau package `shopping`, schéma et compte PostgreSQL `shopping` (datasource et migrations dédiées).
- API : nouvelles routes sous `/api/households/{householdId}/shopping-items`.
- PWA : nouvel onglet « Courses ».
- Données de dev : base à réinitialiser (nouveau schéma).

## Hors périmètre

- Mode hors-ligne (ajout ou cochage sans réseau en magasin).
- Temps réel par WebSocket ou SSE (le rafraîchissement périodique suffit pour deux personnes).
- Plusieurs listes par foyer, rayons, quantités structurées, prix, historique ou suggestions d'articles.
- Lien avec le budget (le total des courses n'est pas saisi automatiquement).
