## Context

- Domaine `household` (schéma `household`) : agrégats `Household` (nom, membres) et `Invitation` (un code actif par foyer, régénérer annule le précédent). Événements de contrat `HouseholdCreatedV1`, `MemberJoinedV1`, `MembershipKnownV1` (rappel au démarrage), distribués par la boîte d'envoi.
- Chaque autre domaine (`budget`, `bills`, `vehicle`, `shopping`, `reminder`) tient une **copie locale des membres** (`household_member`) qui sert à son contrôle d'accès (`…Access`). Retirer un membre doit donc mettre à jour ces copies, sinon il garderait l'accès.

## Goals / Non-Goals

**Goals** : propriétaire unique et transmissible ; retrait, départ, annulation de code, suppression ; aucune donnée ni fichier orphelin ; accès coupé partout, même après un arrêt du serveur.
**Non-Goals** : rôles multiples, corbeille, renommage.

## Decisions

### Domaine `household`

- `Household` gagne `owner` (`MemberId`). Invariants : le propriétaire est membre ; il ne peut ni se retirer ni quitter le foyer.
- Méthodes : `transferOwnership(by, to)`, `removeMember(by, member)` (seul le propriétaire, pas lui-même), `leave(member)` (pas le propriétaire), `delete(by)` (seul le propriétaire : produit `HouseholdDeleted`).
- `Invitation` : génération et `cancel()` réservées au propriétaire (vérifié par le use case via le foyer). Le statut passe à « annulée » ; le code n'est plus accepté. La consultation ne renvoie le code actif qu'au propriétaire.
- `Household.rename(by, name)` : réservé au propriétaire.
- Migration `household` V4 : colonne `owner_id`, remplie avec le plus ancien membre de chaque foyer ; `NOT NULL`.
- Suppression : le foyer, ses appartenances et ses invitations sont effacés (suppression définitive en base).
- Routes (toutes contrôlées par `HouseholdAccess`, 404 pour un non-membre, 403 pour un membre sans le droit) :
  - `PUT /api/households/{h}/owner` `{ memberId }` ;
  - `DELETE /api/households/{h}/members/{memberId}` (retrait) ;
  - `POST /api/households/{h}/leave` ;
  - `POST /api/households/{h}/invitations` (existante, désormais réservée au propriétaire) et `DELETE /api/households/{h}/invitations` (annule le code actif) ;
  - `PUT /api/households/{h}` `{ name }` (renommage) ;
  - `DELETE /api/households/{h}` avec `{ confirmName }` égal au nom du foyer.
- La consultation renvoie `ownerId` ; la PWA en déduit les actions permises.

### Événements de contrat (boîte d'envoi `household`)

- `MemberLeftV1(householdId, memberId, ownerId, occurredAt)` : retrait ou départ.
- `HouseholdDeletedV1(householdId, occurredAt)`.
- Idempotents côté destinataires (supprimer ce qui n'existe plus est sans effet).

### Réactions des autres domaines (chacun dans son schéma)

| Domaine | `MemberLeftV1` | `HouseholdDeletedV1` |
|---|---|---|
| `budget` | retire le membre de sa copie ; ses dépenses passées restent, affichées « Ancien membre » | supprime dépenses, catégories, liens de charges, membres |
| `bills` | retire le membre ; les charges qu'il payait passent au propriétaire, celles dont il était bénéficiaire passent à « Commun » | supprime charges, échéances, factures (fichiers compris), membres |
| `vehicle` | retire le membre | supprime véhicules, carnets, pièces jointes (fichiers compris), membres |
| `shopping` | retire le membre | supprime sous-listes, articles, historique, membres |
| `reminder` | retire le membre | supprime échéances suivies, membres |

- Les fichiers sont supprimés **après** validation de la transaction (même règle qu'aujourd'hui pour une pièce jointe supprimée).
- `MembershipKnownV1` ne rappelle que les membres actuels : un membre retiré ne réapparaît pas au redémarrage.

### PWA (espace Foyer)

- Liste des membres : badge « Propriétaire » ; pour le propriétaire, un menu par membre (« Transmettre la propriété », « Retirer du foyer »), avec confirmation.
- Code d'invitation : bouton « Annuler le code ».
- Bas de page : « Quitter le foyer » (membre) ou zone « Supprimer le foyer » (propriétaire), dans une fenêtre qui demande de retaper le nom.
- Foyer inaccessible (retiré, supprimé, réponse 404) : la PWA recharge la liste des foyers et bascule sur un autre foyer, ou sur « Créer ou rejoindre » s'il n'en reste aucun, avec un message clair.
- Noms des membres partis : « Ancien membre » dans le budget et les charges.

## Risks / Trade-offs

- Suppression définitive : aucune annulation possible → confirmation par le nom, texte explicite, propriétaire seul.
- Un membre retiré ouvre encore l'application : ses requêtes reçoivent 404 dès la validation du retrait (contrôle par la copie locale, mise à jour par la boîte d'envoi en quelques millisecondes ; au pire 5 s après une erreur).
- Charges réattribuées au propriétaire : choix par défaut, à confirmer par Dorian (question 2).

## Migration Plan

Migration `household` V4 (colonne `owner_id`, remplie). Aucune autre migration. Retour arrière : version précédente, la colonne est ignorée.

## Open Questions — tranchées par Dorian le 2026-11-19

1. **Quitter un foyer** : oui.
2. **Charges d'un membre retiré** : pas de résiliation ; payeur transféré au propriétaire, bénéficiaire passé à « Commun ».
3. **Suppression** : immédiate et définitive.
4. **Invitations** : **seul le propriétaire** génère, copie et annule le code d'invitation (changement par rapport à aujourd'hui, où tout membre peut inviter). Les autres membres ne voient pas le code.
5. **Renommer le foyer** : oui, par le propriétaire (`PUT /api/households/{h}` `{ name }`, mêmes règles de nom qu'à la création).
