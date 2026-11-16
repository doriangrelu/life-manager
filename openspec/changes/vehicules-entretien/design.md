## Context

Le socle (comptes, foyers) et le budget sont en place. Le foyer publie déjà `HouseholdCreatedV1` et `MemberJoinedV1` après commit ; le budget s'en sert pour tenir sa copie locale des membres. Le domaine véhicule suit exactement le même modèle (`docs/architecture.md`).

## Goals / Non-Goals

**Goals:**
- Un domaine `vehicle` autonome : il vérifie seul qui est membre d'un foyer.
- Des échéances lisibles par un futur domaine de rappels, sans qu'il dépende du code véhicule (événements de domaine prêts à être traduits en contrat).

**Non-Goals:**
- Rappels, notifications (change suivant).
- Calcul automatique des échéances.

## Decisions

### 1. Domaine `vehicle`, schéma `vehicle`

Nouveau package de premier niveau `vehicle`, datasource `vehicle` (compte PostgreSQL `vehicle_app`), migrations Flyway dans `db/migration/vehicle`. Aucune clé étrangère vers un autre schéma.

### 2. Agrégats

**`Vehicle`** — invariants : nom valide, un seul foyer.
- Value objects : `VehicleId`, `HouseholdId`, `MemberId` (propres au domaine), `VehicleName` (1 à 40 caractères), `LicensePlate` (1 à 12 caractères, mis en majuscules, facultatif), `Deadlines` (prochain contrôle technique et prochaine révision, deux `LocalDate` facultatives), et `VehicleDetails` qui regroupe ce qui se saisit (nom, immatriculation, échéances).
- Méthodes : `register`, `update`, `markRemoved`.
- Événements de domaine : `VehicleRegistered`, `VehicleUpdated`, `VehicleRemoved` (portent le foyer, le nom et les échéances). Sans consommateur dans ce change ; le change `rappels-notifications` les traduira en événements de contrat.

**`MaintenanceEntry`** — invariants : date pas dans le futur (fuseau `life-manager.time-zone`), bornes du kilométrage et du coût.
- Value objects : `MaintenanceEntryId`, `MaintenanceKind` (énumération : `SERVICE`, `OIL_CHANGE`, `TECHNICAL_INSPECTION`, `TIRES`, `REPAIR`, `OTHER`), `Mileage` (0 à 2 000 000 km), `Cost` (centimes `long`, 0 à 100 000 €), `MaintenanceNote` (140 caractères), et `MaintenanceDetails` qui les regroupe avec la date.
- Méthodes : `record` ; suppression par le repository.
- Référence le véhicule par `VehicleId`.

Deux agrégats séparés : une opération du carnet ne protège aucune règle de la fiche, et le carnet peut grandir sans alourdir le chargement d'un véhicule. La seule règle qui les relie (l'opération appartient à un véhicule du foyer du demandeur) est vérifiée par un service applicatif.

### 3. Membres du foyer

Comme le budget : écouteur `HouseholdEventsListener` (`infrastructure/in/event`) sur `HouseholdCreatedV1` et `MemberJoinedV1`, use case `RegisterHouseholdMember`, table `vehicle.household_member` avec insertion idempotente.

### 4. Services applicatifs

- `VehicleAccess` : vérifie l'appartenance au foyer (sinon `404`) et charge un véhicule du foyer (sinon `404`). Utilisé par tous les use cases.

### 5. Use cases

Véhicules : `ListVehicles`, `RegisterVehicle`, `UpdateVehicle`, `RemoveVehicle`.
Carnet : `ListMaintenanceEntries`, `RecordMaintenance`, `DeleteMaintenanceEntry`.
Membres : `RegisterHouseholdMember` (appelé par l'écouteur).

`RemoveVehicle` supprime le véhicule ; ses opérations partent avec lui par `ON DELETE CASCADE` (même schéma, même domaine) : le use case ne modifie qu'un agrégat côté code.

### 6. Base de données (schéma `vehicle`)

- `household_member(id, household_id, member_id, UNIQUE (household_id, member_id))`
- `vehicle(id, household_id, name, license_plate NULL, technical_inspection_due DATE NULL, service_due DATE NULL)` + index `(household_id)`
- `maintenance_entry(id, vehicle_id FK ON DELETE CASCADE, performed_on DATE, kind VARCHAR, mileage_km INTEGER NULL CHECK ≥ 0, cost_cents BIGINT NULL CHECK ≥ 0, note VARCHAR(140) NULL, recorded_by, recorded_at)` + index `(vehicle_id, performed_on)`

### 7. API (`/api/households/{householdId}/vehicles`)

| Route | Use case |
|---|---|
| `GET` / `POST /vehicles` | lister / ajouter |
| `PUT /vehicles/{id}` / `DELETE /vehicles/{id}` | modifier / supprimer |
| `GET` / `POST /vehicles/{id}/maintenance` | carnet / ajouter une opération |
| `DELETE /vehicles/{id}/maintenance/{entryId}` | supprimer une opération |

Dates au format ISO (`2027-03-01`), coût en centimes (`costCents`), type en anglais majuscule (`SERVICE`…), traduit par la PWA.

### 8. PWA

Troisième onglet « Véhicules ». Liste des véhicules avec leurs échéances ; une échéance passée ou à moins de 30 jours est mise en évidence. Fiche d'un véhicule : modification, échéances, carnet (ajout rapide, suppression). Saisie du coût en euros convertie en centimes par le module `money.ts` existant.

## Risks / Trade-offs

- **Copie des membres désynchronisée** (pas d'outbox) → risque accepté, identique au budget.
- **Troisième copie des membres** (budget, véhicule, bientôt courses) → duplication assumée par l'architecture (domaines découpables) ; une seule table par domaine, peu de lignes.
- **Suppression définitive** d'un véhicule et de son carnet → pas de corbeille, confirmation dans la PWA.

## Migration Plan

Nouveau schéma et nouveau compte PostgreSQL dans le script d'initialisation de dev et dans `db/test-init.sql` ; migration Flyway `V1` du schéma `vehicle`. Base de dev à recréer.

## Décisions prises sans Dorian

1. **Échéances saisies à la main** — retenu : deux dates facultatives modifiables sur la fiche. Alternatives : calcul automatique depuis le carnet (CT + 2 ans, révision + 1 an), échéances libres en nombre illimité. Raison : le plus simple, et les règles réelles varient (premier CT à 4 ans, révision selon kilométrage). Revenir en arrière : ajouter un calcul de suggestion dans la PWA ou un service applicatif, sans changer le schéma.
2. **Deux types d'échéance seulement (CT, révision)** — alternatives : assurance, vignette, échéances libres. Raison : périmètre MVP de `docs/vision.md`. Revenir en arrière : remplacer les deux colonnes par une table `deadline(vehicle_id, kind, due_on)`.
3. **Suppression définitive d'un véhicule avec son carnet** — alternative : archivage. Raison : simplicité ; un véhicule vendu n'a plus d'intérêt au quotidien. Revenir en arrière : ajouter une colonne `archived` comme pour les catégories du budget.
4. **Copie locale des membres propre au domaine véhicule** — alternative : un port synchrone vers `household` ou une table partagée. Raison : l'architecture interdit le partage de tables et veut des domaines découpables. Revenir en arrière : sans objet (règle d'architecture).
5. **Kilométrage uniquement sur les opérations du carnet** — alternative : kilométrage courant sur la fiche. Raison : évite une donnée à tenir à jour sans usage dans le MVP. Revenir en arrière : ajouter un champ à `VehicleDetails`.
6. **Coût d'entretien non relié au budget** — alternative : créer une dépense du budget par événement. Raison : éviter les doublons de saisie involontaires et un couplage précoce. Revenir en arrière : publier un événement de contrat et l'écouter côté budget.
