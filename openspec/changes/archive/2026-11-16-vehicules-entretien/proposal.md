## Why

Suivre l'entretien des véhicules (carnet, contrôle technique, révision) est le deuxième objectif de la vision (`docs/vision.md`). Aujourd'hui ces informations sont dispersées (factures papier, mémoire) et les échéances passent inaperçues. Ce change pose les données ; les rappels automatiques viendront avec `rappels-notifications`.

## What Changes

- Nouveau domaine **vehicle**, propre à chaque foyer, dans son propre schéma de base.
- **Fiche véhicule** : nom (« Clio », « Vélo cargo »…), immatriculation facultative ; ajout, modification et suppression par n'importe quel membre du foyer.
- **Échéances** : chaque véhicule porte deux dates facultatives, prochain contrôle technique et prochaine révision, saisies par les membres.
- **Carnet d'entretien** : opérations datées (révision, vidange, contrôle technique, pneus, réparation, autre) avec kilométrage, coût en euros et note facultatifs ; ajout et suppression.
- Le domaine **vehicle** connaît les membres de chaque foyer grâce aux événements de contrat déjà publiés par le foyer (`HouseholdCreatedV1`, `MemberJoinedV1`).
- **PWA** : nouvel onglet « Véhicules » dans le foyer : liste avec échéances mises en évidence, fiche d'un véhicule avec son carnet.

## Capabilities

### New Capabilities

- `vehicules` : fiches des véhicules d'un foyer et leurs échéances (contrôle technique, révision).
- `carnet-entretien` : historique des opérations d'entretien d'un véhicule.

### Modified Capabilities

_Aucune._

## Impact

- Backend : nouveau package `vehicle`, schéma et compte PostgreSQL `vehicle` (datasource et migrations dédiées).
- API : nouvelles routes sous `/api/households/{householdId}/vehicles`.
- PWA : troisième onglet, deux écrans.
- Données de dev : base à réinitialiser (nouveau schéma ; les foyers existants ne sont pas connus du domaine véhicule).

## Hors périmètre

- Rappels et notifications (change `rappels-notifications`).
- Calcul automatique de la prochaine échéance à partir du carnet (ex. CT + 2 ans).
- Autres échéances (assurance, vignette, crédit-bail), pièces jointes (factures, carte grise).
- Lien avec le budget (le coût d'un entretien n'est pas une dépense du budget).
- Suivi du kilométrage dans le temps, consommation de carburant, statistiques.
