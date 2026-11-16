## 1. Socle du domaine vehicle

- [ ] 1.1 [infra] Schéma `vehicle` et compte `vehicle_app` dans `infra/postgres/init` et `docker-compose.yml`, schéma ajouté à `db/test-init.sql` ; vérifié en recréant la base de dev
- [x] 1.2 [vehicle/infrastructure/out] Datasource et Flyway `vehicle`, migration `V1` (household_member, vehicle, maintenance_entry) ; `vehicle` ajouté aux domaines d'ArchUnit et des tests d'intégration ; vérifié par `./mvnw verify`
- [x] 1.3 [vehicle/domain] Value objects (`VehicleId`, `HouseholdId`, `MemberId`, `VehicleName`, `LicensePlate`, `Deadlines`, `VehicleDetails`, `MaintenanceEntryId`, `MaintenanceKind`, `Mileage`, `Cost`, `MaintenanceNote`, `MaintenanceDetails`) ; vérifié par des tests unitaires des cas limites

## 2. Membres du foyer côté véhicule

- [x] 2.1 [vehicle/application + infrastructure] Port et table `household_member`, use case `RegisterHouseholdMember`, écouteur des événements du foyer, service `VehicleAccess` ; vérifié par le test d'intégration de 3.3 (membre arrivé → accès, étranger → 404)

## 3. Véhicules

- [x] 3.1 [vehicle/domain] Agrégat `Vehicle` et ses événements ; vérifié par des tests unitaires
- [x] 3.2 [vehicle/infrastructure/out] Repository, mapper et adaptateur des véhicules ; vérifié par le test de 3.3
- [x] 3.3 [vehicle/application + infrastructure/in/web] `ListVehicles`, `RegisterVehicle`, `UpdateVehicle`, `RemoveVehicle` et routes `/vehicles` ; vérifié par un test d'intégration (ajout, immatriculation en majuscules, partage, modification, suppression, 400 et 404)

## 4. Carnet d'entretien

- [x] 4.1 [vehicle/domain] Agrégat `MaintenanceEntry` ; vérifié par des tests unitaires (date future refusée)
- [x] 4.2 [vehicle/infrastructure/out] Repository, mapper et adaptateur du carnet ; vérifié par le test de 4.3
- [x] 4.3 [vehicle/application + infrastructure/in/web] `ListMaintenanceEntries`, `RecordMaintenance`, `DeleteMaintenanceEntry` et routes `/maintenance` ; vérifié par un test d'intégration (tri, bornes, date future, suppression, cascade à la suppression du véhicule, 404)

## 5. PWA

- [ ] 5.1 [frontend] Client d'API véhicules, types, fonctions d'échéance (passée / proche) avec tests Vitest ; vérifié par `bun run check` et `bun run test`
- [ ] 5.2 [frontend] Onglet « Véhicules » : liste, ajout, échéances mises en évidence ; vérifié dans le navigateur
- [ ] 5.3 [frontend] Fiche véhicule : modification, suppression, carnet (ajout, suppression) ; vérifié dans le navigateur en affichage mobile

## 6. Intégration

- [ ] 6.1 [doc] `docs/architecture.md` (domaine vehicle) et README à jour ; parcours à deux comptes vérifié de bout en bout
