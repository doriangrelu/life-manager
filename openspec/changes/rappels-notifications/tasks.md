## 1. Événements de contrat du véhicule

- [x] 1.1 [vehicle/contract + infrastructure/out/event] `VehicleDeadlinesChangedV1` et `VehicleRemovedV1` publiés après commit par un relais ; vérifié par un test d'intégration qui ajoute, modifie, supprime un véhicule et reçoit les événements (aucun sur une modification refusée)

## 2. Socle du domaine reminder

- [ ] 2.1 [infra] Schéma `reminder` et compte `reminder_app` dans `infra/postgres/init`, `docker-compose.yml`, `.env.example` et `db/test-init.sql` ; vérifié en recréant la base de dev
- [x] 2.2 [reminder/infrastructure/out] Datasource et Flyway `reminder`, migration `V1` (household_member, deadline) ; `reminder` ajouté aux domaines d'ArchUnit et des tests d'intégration ; vérifié par `./mvnw verify`
- [x] 2.3 [reminder/domain] Value objects et agrégat `Deadline` avec la règle de rappel (30 jours) ; vérifié par des tests unitaires (veille, jour même, J+30, J+31)

## 3. Rappels

- [x] 3.1 [reminder/application + infrastructure] Membres du foyer (écouteur, use case, table, `ReminderAccess`)
- [x] 3.2 [reminder/application + infrastructure/in/event] `TrackVehicleDeadlines`, `ForgetVehicleDeadlines` et écouteur des événements du véhicule ; repository, mapper et adaptateur des échéances
- [x] 3.3 [reminder/application + infrastructure/in/web] `ListReminders` et route `GET /reminders` ; vérifié par un test d'intégration de bout en bout passant par l'API véhicules (rappel en retard et proche, échéance repoussée, véhicule supprimé, 404)

## 4. PWA

- [x] 4.1 [frontend] Client d'API, libellés et mémo « déjà notifié aujourd'hui » avec tests Vitest ; vérifié par `bun run check` et `bun run test`
- [ ] 4.2 [frontend] Bandeau de rappels au-dessus des onglets et activation des notifications système ; vérifié dans le navigateur

## 5. Intégration

- [ ] 5.1 [doc] `docs/architecture.md` (domaine reminder) à jour ; parcours vérifié de bout en bout
