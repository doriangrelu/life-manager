## Context

Le domaine `vehicle` porte les échéances (contrôle technique, révision) et produit déjà des événements de domaine `VehicleRegistered`, `VehicleUpdated`, `VehicleRemoved`, sans les publier hors du domaine. La PWA est installable (service worker généré par `vite-plugin-pwa`), mais n'a aucun mécanisme de notification. Le backend n'a ni tâche planifiée, ni envoi de courriel, ni clés Web Push.

## Goals / Non-Goals

**Goals:**
- Un domaine `reminder` qui ne dépend de `vehicle` que par ses événements de contrat, prêt à recevoir d'autres sources d'échéances en V2.
- Des rappels visibles dès l'ouverture de l'application, et une notification système facultative, sans aucun secret ni service externe.

**Non-Goals:**
- Notification quand l'application est fermée (Web Push), courriel.
- Rappels saisis à la main.

## Decisions

### 1. Événements de contrat du domaine véhicule

Nouveau relais `VehicleContractEventRelay` (`vehicle/infrastructure/out/event`), identique à celui du foyer : `@TransactionalEventListener(AFTER_COMMIT, transactionManager = "vehicle")` sur les événements de domaine, publication sur le bus Micronaut de :
- `VehicleDeadlinesChangedV1(UUID vehicleId, UUID householdId, String vehicleName, LocalDate technicalInspectionDue, LocalDate serviceDue, Instant occurredAt)` — pour `VehicleRegistered` et `VehicleUpdated` ; une échéance absente vaut `null`.
- `VehicleRemovedV1(UUID vehicleId, UUID householdId, Instant occurredAt)`.

`LocalDate` est un type simple, sérialisé en ISO (`2027-03-01`) : il reste compatible avec un futur broker. L'événement porte l'état complet des échéances (et pas un delta) : le recevoir deux fois ou dans le désordre ne fausse pas la copie au-delà du dernier reçu.

### 2. Domaine `reminder`, schéma `reminder`

Package de premier niveau `reminder`, datasource `reminder` (compte PostgreSQL `reminder_app`), migrations dans `db/migration/reminder`. Copie des membres du foyer comme dans les autres domaines (`HouseholdEventsListener`, `RegisterHouseholdMember`, table `household_member`).

### 3. Agrégat `Deadline`

Une échéance suivie = un agrégat. Invariants : un sujet lisible, une date, un seul foyer, une seule source.
- Value objects : `DeadlineId`, `HouseholdId`, `MemberId`, `SourceId` (identifiant de l'objet d'origine, ici le véhicule), `DeadlineKind` (`TECHNICAL_INSPECTION`, `SERVICE`), `Subject` (nom affiché, 1 à 40 caractères).
- Règle métier portée par l'agrégat : `reminderOn(LocalDate today)` donne un `Reminder` (statut `OVERDUE` ou `DUE_SOON`, jours restants) si l'échéance est passée ou dans les `REMINDER_WINDOW_DAYS = 30` jours, rien sinon.
- `Deadline.fromVehicle(...)` construit zéro, une ou deux échéances à partir d'un véhicule.

### 4. Use cases

- `TrackVehicleDeadlines` (écouteur de `VehicleDeadlinesChangedV1`) : remplace les échéances de ce véhicule par celles reçues. Il modifie jusqu'à deux agrégats `Deadline` d'un coup : c'est le remplacement d'une copie, sans invariant entre eux, dans une seule transaction pour ne jamais exposer un état à moitié remplacé.
- `ForgetVehicleDeadlines` (écouteur de `VehicleRemovedV1`) : supprime les échéances du véhicule.
- `ListReminders` : vérifie l'appartenance au foyer, charge ses échéances, garde celles qui donnent un rappel à la date du jour (fuseau `life-manager.time-zone`), triées par date.
- `RegisterHouseholdMember`.
- Service `ReminderAccess` : appartenance au foyer (sinon `404`).

### 5. Base de données (schéma `reminder`)

- `household_member(id, household_id, member_id, UNIQUE (household_id, member_id))`
- `deadline(id, household_id, source_id, kind VARCHAR, subject VARCHAR(40), due_on DATE, UNIQUE (source_id, kind))` + index `(household_id)`

### 6. API

`GET /api/households/{householdId}/reminders` → `[{ deadlineId, kind, subject, dueOn, status, daysLeft }]`.

### 7. PWA : canal de notification

- Composant `RemindersBanner` au-dessus des onglets du foyer : liste compacte (« Révision — Clio : en retard de 46 jours »). Chargé à l'ouverture du foyer et au retour de visibilité.
- Bouton « Activer les notifications » si `Notification.permission === 'default'` (la demande d'autorisation exige un geste de l'utilisateur).
- Si l'autorisation est accordée : pour chaque rappel non encore notifié aujourd'hui sur cet appareil (mémo `localStorage` : identifiant d'échéance + date du jour), `ServiceWorkerRegistration.showNotification` (ou `new Notification` à défaut). Le stockage indisponible ne bloque rien.

## Risks / Trade-offs

- **Pas de notification application fermée** → un membre qui n'ouvre pas l'application n'est pas prévenu. Acceptable pour le MVP (usage quotidien prévu pour le budget et les courses) ; Web Push en V2.
- **iOS** → les notifications web n'y fonctionnent que pour une PWA installée sur l'écran d'accueil (iOS 16.4+) ; le bandeau reste toujours visible.
- **Événement perdu** (pas d'outbox) → la copie des échéances peut être en retard d'une modification ; corrigée à la modification suivante du véhicule. Risque accepté, identique aux autres domaines.
- **Véhicules créés avant ce change** → inconnus du domaine reminder ; base de dev à recréer.

## Migration Plan

Nouveau schéma et compte PostgreSQL dans le script d'init de dev, `docker-compose.yml`, `.env.example` et `db/test-init.sql` ; migration Flyway `V1` du schéma `reminder`.

## Décisions prises sans Dorian

1. **Canal : bandeau dans l'application + notification système locale à l'ouverture, pas de Web Push** — alternatives : Web Push (clés VAPID, abonnements stockés, tâche planifiée côté serveur, bibliothèque de chiffrement à valider en image native), courriel (serveur SMTP et secrets), rien d'autre que le bandeau. Raison : le plus simple qui fonctionne dans la PWA sur mobile et desktop, sans secret ni service externe, et testable de bout en bout. Revenir en arrière / aller plus loin : ajouter un domaine d'abonnements Web Push et une tâche planifiée qui lit `ListReminders` ; l'API et le domaine reminder restent valables.
2. **Délai de rappel fixe de 30 jours** — alternative : délai réglable par foyer ou par type. Raison : simplicité ; cohérent avec la mise en évidence déjà faite dans l'onglet Véhicules. Revenir en arrière : remplacer la constante `REMINDER_WINDOW_DAYS` par un réglage du foyer.
3. **Une notification par rappel et par jour, mémorisée par appareil** — alternatives : acquittement partagé entre membres, report. Raison : pas d'état serveur à gérer ; un rappel ignoré revient le lendemain, ce qui est le comportement voulu pour une échéance. Revenir en arrière : ajouter un agrégat d'acquittement côté serveur.
4. **Copie complète des échéances dans le domaine reminder** (plutôt qu'interroger `vehicle`) — imposée par l'architecture (communication par événements uniquement). Revenir en arrière : sans objet.
5. **`LocalDate` dans un événement de contrat** — alternative : `String` ISO. Raison : type simple du JDK, sérialisé en ISO par Micronaut Serde, plus sûr qu'une chaîne libre. Revenir en arrière : changer le type dans une version `V2` de l'événement.
