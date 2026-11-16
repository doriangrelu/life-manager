## Why

Les échéances des véhicules (contrôle technique, révision) sont saisies depuis `vehicules-entretien`, mais rien ne prévient quand elles approchent. La vision (`docs/vision.md`) demande des « rappels automatiques » ; le socle MVP liste les rappels (notifications).

## What Changes

- Nouveau domaine **reminder**, dans son propre schéma de base : il tient une copie des échéances du foyer et décide lesquelles méritent un rappel.
- Le domaine **vehicle** publie des événements de contrat : `VehicleDeadlinesChangedV1` (ajout ou modification d'un véhicule) et `VehicleRemovedV1`. Le domaine reminder les écoute ; il écoute aussi les événements du foyer pour connaître ses membres.
- **Règle de rappel** : une échéance passée est « en retard », une échéance dans les 30 jours est « bientôt » ; au-delà, pas de rappel.
- **API** : liste des rappels d'un foyer, triés de la plus urgente à la moins urgente.
- **PWA** : bandeau « Rappels » en haut du foyer, visible sur tous les onglets ; sur demande de l'utilisateur, notifications système de l'appareil, affichées à l'ouverture de l'application, une fois par jour et par rappel.

## Capabilities

### New Capabilities

- `rappels` : calcul des rappels d'échéances d'un foyer et leur affichage dans la PWA, avec notification système facultative.

### Modified Capabilities

- `vehicules` : les changements d'échéances et la suppression d'un véhicule sont publiés aux autres domaines.

## Impact

- Backend : nouveau package `reminder`, schéma et compte PostgreSQL `reminder` ; événements de contrat ajoutés au package `vehicle`.
- API : nouvelle route `GET /api/households/{householdId}/reminders`.
- PWA : bandeau de rappels, bouton d'activation des notifications.
- Données de dev : base à réinitialiser (nouveau schéma ; les véhicules existants ne sont pas connus du domaine reminder).

## Hors périmètre

- Notifications quand l'application est fermée (Web Push), courriel, SMS.
- Rappels génériques saisis à la main, rappels d'autres domaines (factures, documents) : V2.
- Réglage du délai de rappel (30 jours fixes), report (« me rappeler plus tard ») ou acquittement partagé entre appareils.
