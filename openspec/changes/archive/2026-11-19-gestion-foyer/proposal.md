## Why

Aujourd'hui, une fois entré dans un foyer, on y reste : personne ne peut retirer un membre, annuler un code d'invitation qui circule, ni supprimer un foyer créé par erreur. C'est le contrôle qui manque avant d'ouvrir l'application à d'autres familles (lot 3 de `docs/feuille-de-route.md`).

## What Changes

- **Propriétaire du foyer** : le créateur au départ (les foyers existants reçoivent leur plus ancien membre). Il peut **transmettre** ce rôle à un autre membre. Il est signalé dans la liste des membres.
- **Retirer un membre** : réservé au propriétaire. Le membre retiré perd aussitôt l'accès au foyer et à toutes ses données.
- **Quitter un foyer** : tout membre sauf le propriétaire, qui doit d'abord transmettre le rôle ou supprimer le foyer.
- **Invitations réservées au propriétaire** : lui seul génère, voit et **annule** le code d'invitation (choix de Dorian).
- **Renommer le foyer** : par le propriétaire.
- **Supprimer un foyer** : réservé au propriétaire, avec une confirmation forte (retaper le nom). **Toutes** les données du foyer sont effacées dans chaque domaine (dépenses, catégories, charges et factures, véhicules et pièces jointes, courses, rappels).
- **PWA** : écran Foyer enrichi (propriétaire, retrait, départ, annulation du code, zone « Supprimer le foyer ») ; quand le foyer affiché n'est plus accessible, la PWA bascule sur un autre foyer ou sur « Créer ou rejoindre ».

## Capabilities

### New Capabilities

Aucune.

### Modified Capabilities

- `foyer` : propriétaire, retrait, départ, annulation d'invitation, suppression, parcours PWA.

## Impact

- `household` : propriétaire dans l'agrégat (colonne `owner_id`, migration additive), nouveaux use cases et routes, nouveaux événements de contrat `MemberLeftV1` et `HouseholdDeletedV1` (par la boîte d'envoi).
- `budget`, `bills`, `vehicle`, `shopping`, `reminder` : réagissent au départ d'un membre (copie locale des membres) et à la suppression d'un foyer (effacement de leurs données et fichiers).
- PWA : écran Foyer, sélection du foyer.

## Hors périmètre

- Plusieurs propriétaires ou rôles plus fins (lecture seule…).
- Corbeille ou délai de grâce avant suppression (la suppression est immédiate et définitive ; seules les sauvegardes nocturnes en gardent une trace 14 jours).
- Limites et comptes VIP (lot 11).
