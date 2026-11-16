## Why

Saisir une dépense en quelques secondes depuis le mobile est l'objectif n°1 de la vision (`docs/vision.md`). Le socle (comptes, foyers) est en place : c'est le premier vrai domaine métier construit dessus.

## What Changes

- Nouveau domaine **budget**, propre à chaque foyer, dans son propre schéma de base.
- **Dépenses** : saisie rapide (montant en euros, catégorie, date, qui a payé, pour qui — un membre ou « commun », libellé facultatif), modification et suppression par n'importe quel membre.
- **Catégories** : chaque nouveau foyer reçoit une liste par défaut ; ses membres peuvent ajouter, renommer et archiver des catégories.
- **Vue mensuelle** : total du mois, total par catégorie, par payeur et par bénéficiaire, et liste des dépenses du mois.
- Le domaine **foyer** publie désormais des événements de contrat (foyer créé, membre arrivé) : le budget s'en sert pour connaître les membres de chaque foyer et créer les catégories par défaut, sans dépendre du code du foyer.
- **PWA** : écran de saisie rapide pensé pour le mobile et écran du mois, accessibles depuis le foyer sélectionné.

## Capabilities

### New Capabilities

- `depenses` : saisie, modification et suppression des dépenses d'un foyer.
- `categories-depenses` : catégories par défaut et personnalisation par foyer.
- `budget-mensuel` : synthèse et détail des dépenses d'un mois.

### Modified Capabilities

_Aucune : les événements publiés par le foyer ne changent pas son comportement observable._

## Impact

- Backend : nouveau package `budget` et schéma `budget` (datasource et migrations dédiées) ; événements de contrat ajoutés au package `household`.
- API : nouvelles routes sous `/api/households/{householdId}/…`.
- PWA : navigation entre « Budget » et « Foyer », deux nouveaux écrans.
- Données de dev : les foyers créés avant ce change n'ont ni membres connus du budget ni catégories (pas de reprise ; base de dev à réinitialiser).

## Hors périmètre

- Plusieurs devises (tout est en euros).
- Équilibre « qui doit combien à qui » et remboursements.
- Budgets prévisionnels, plafonds par catégorie, alertes.
- Dépenses récurrentes, pièces jointes (tickets), import bancaire ou CSV.
- Graphiques, comparaison entre mois, export.
- Saisie hors-ligne.
