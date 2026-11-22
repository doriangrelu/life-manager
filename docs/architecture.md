# Architecture du backend

Référence à respecter pour tout code backend. En cas de doute non couvert ici : demander avant de coder.

## Principes

- **Clean architecture** : le métier (`domain`) ne dépend de rien de technique ; l'`application` orchestre ; l'`infrastructure` branche le reste du monde.
- **Un monolithe modulaire** : une seule application Micronaut, plusieurs domaines métiers **strictement séparés** (code, base de données, événements). Découper un domaine en microservice ne doit demander qu'un broker externe, rien d'autre.
- **Lisible avant tout** : un humain doit pouvoir entrer dans le code facilement. Petites classes, petites méthodes, noms explicites.
- **Pas de réinvention** : si une librairie connue fait le travail, on l'utilise.

## Domaines métiers

Un domaine = un package de premier niveau, avec son propre schéma de base.

| Domaine | Package | Rôle |
|---|---|---|
| Identité | `identity` | Profil utilisateur local, rattaché au compte Keycloak |
| Foyer | `household` | Foyers, membres, invitations, réglages (suivi des rentrées) |
| Budget | `budget` | Dépenses et rentrées, catégories (sens dépense ou rentrée), vue mensuelle, bilan par période |
| Véhicule | `vehicle` | Fiches véhicules, échéances (CT, révision), kilométrage, pièces d'usure, carnet d'entretien (coût envoyé au budget par `MaintenanceExpenseSentV1` / `MaintenanceExpenseWithdrawnV1`) |
| Courses | `shopping` | Liste de courses partagée par foyer |
| Rappels | `reminder` | Copie des échéances (véhicules, charges) et rappels à afficher ou notifier |
| Charges | `bills` | Charges et rentrées récurrentes (sens `EXPENSE` / `INCOME`), échéances, factures jointes, prévisions de période |
| Projets | `project` | Projets du foyer (budget prévu, état, option « budget à part »), tâches ordonnées avec responsable et date |

Le budget garde une copie des projets (`budget.project_ref`, événements `ProjectChangedV1` / `ProjectDeletedV1`) : il vérifie le rattachement d'une dépense et écarte des totaux, par la vue SQL `budget.counted_expense`, les dépenses d'un projet à budget à part. Les rappels suivent les tâches datées par `TaskDeadlineChangedV1` (type d'échéance `TASK`, 7 jours avant).

Un domaine apparu après la création d'un foyer apprend ses membres par `MembershipKnownV1`, rappelé pour chaque membre au démarrage (le traitement doit accepter les doublons).

## Arborescence des packages

Package racine : `io.github.doriangrelu.lifemanager`. Un package par **type d'objet** : on ne mélange jamais agrégats, value objects, DTO, etc.

```
io.github.doriangrelu.lifemanager
├── kernel                          # briques DDD partagées, aucun lien vers un domaine
│   ├── domain
│   │   ├── aggregate               # AggregateRoot
│   │   ├── entity                  # Entity (égalité par identifiant)
│   │   ├── event                   # DomainEvent
│   │   ├── exception               # InvalidValueException, NotFoundException, ConflictException
│   │   └── validation              # Require : contrôles de forme des value objects
│   ├── application/port/out        # DomainEventPublisher
│   └── infrastructure/event        # publication sur le bus Micronaut
├── platform                        # technique transverse, sans métier
│   └── infrastructure
│       ├── security                # introspection Keycloak, utilisateur authentifié
│       └── web                     # traduction des erreurs en réponses HTTP
└── <domaine>
    ├── contract/event              # événements publics du domaine (seule partie visible des autres)
    ├── domain
    │   ├── aggregate               # racines d'agrégat
    │   ├── entity                  # entités internes à un agrégat
    │   ├── valueobject
    │   ├── event                   # événements métier internes
    │   └── exception
    ├── application
    │   ├── port/in                 # interface de chaque use case
    │   │   ├── command             # entrées des use cases qui modifient
    │   │   ├── query               # entrées des use cases qui lisent
    │   │   └── result              # sorties des use cases
    │   ├── port/out                # ce dont l'application a besoin (repositories…)
    │   ├── usecase                 # implémentations des ports in
    │   └── service                 # code commun à plusieurs use cases
    └── infrastructure
        ├── in
        │   ├── web/controller
        │   ├── web/dto
        │   └── event               # écoute des événements d'autres domaines
        └── out
            ├── persistence/entity      # tables (Micronaut Data)
            ├── persistence/repository  # repositories Micronaut Data
            ├── persistence/mapper      # domaine <-> table (écrit à la main)
            ├── persistence/adapter     # implémentation des ports out
            └── event                   # domaine -> contrat, publication
```

## Règles de dépendance

Vérifiées automatiquement par des tests **ArchUnit**.

- `domain` → uniquement `kernel.domain` et le JDK. **Aucune** annotation Micronaut, JPA ou Jakarta.
- `application` → `domain`, `kernel`. Les annotations Micronaut sont autorisées (`@Singleton`, `@Transactional`).
- `infrastructure` → tout le reste du domaine, frameworks, librairies.
- Les contrôleurs ne dépendent que des **ports in**, jamais des classes `usecase`.
- Un domaine ne voit d'un autre domaine **que son package `contract`**, et seulement depuis `infrastructure.in.event`.
- `kernel` et `platform` ne dépendent d'aucun domaine.

## Cloisonnement des foyers (sécurité)

Toute route `/api/households/{householdId}/…` reçoit un foyer choisi par le client : il **doit** appartenir au demandeur.

- Chaque domaine a un service `application/service/…Access` (copie locale des membres) : un use case commence par lui, avant toute lecture, écriture ou fichier. Foyer d'un autre, ou ressource d'un autre foyer : **404** (on ne révèle pas son existence).
- **ArchUnit** (`use_cases_check_household_membership`) : tout use case dépend d'un `…Access` (ou d'un `…Recorder` qui l'appelle d'abord), sauf une liste explicite de use cases internes (événements, tâches planifiées, compte du demandeur). Un nouveau use case sans contrôle fait échouer le build.
- **Départ d'un membre et suppression d'un foyer** : le domaine foyer publie `MemberLeftV1` et `HouseholdDeletedV1`. Chaque domaine a un port `HouseholdData` (copie des membres, effacement de toutes ses données) et deux use cases internes `ForgetHouseholdMember` / `ForgetHousehold` ; les fichiers sont supprimés **après** la validation en base. Un nouveau domaine qui garde des données par foyer DOIT implémenter les deux.
- **Propriétaire** : seul lui renomme, invite, retire, transmet ou supprime (`Household.requireOwner`, 403 sinon).
- **Balayage des routes** (`HouseholdIsolationTest`) : chaque route de foyer, y compris une route ajoutée demain, est appelée par un non-membre. Aucune ne doit réussir ni renvoyer une donnée du foyer.

## Use cases et services applicatifs

- **Un use case = une seule action** (`CreateHousehold`, `JoinHousehold`…). Interface dans `port/in`, implémentation `…UseCase` dans `usecase`.
- Un use case peut être `@Transactional`. Il ne modifie qu'**un seul domaine**.
- Entrées/sorties : des `record` à types simples (`…Command`, `…Query`, résultats) rangés dans `port/in/command`, `port/in/query` et `port/in/result`.
- Dès que deux use cases partagent de la logique, elle part dans un **service applicatif** (`application/service`). Les règles qui ne concernent qu'un agrégat restent dans l'agrégat.

## Agrégats

- **Petits et simples** : un agrégat existe pour protéger des invariants métier. Pas d'invariant commun → pas le même agrégat.
- Les agrégats se référencent **par identifiant** (value object), jamais par objet.
- Tout changement d'état passe par une méthode métier de la racine (pas de setters).
- La racine hérite de `AggregateRoot` (kernel) et enregistre ses événements ; le use case les publie après sauvegarde.
- Un use case ne modifie en principe qu'un agrégat ; s'il en modifie deux du même domaine, c'est justifié dans le design.

## Validation des données

- **Impossible de construire un objet métier invalide** : chaque value object vérifie sa forme dans son constructeur (`record` compact) et lève `InvalidValueException`.
- Les agrégats vérifient leurs invariants dans leurs méthodes et lèvent une exception métier dédiée.
- Les DTO HTTP sont validés en entrée avec **Jakarta Bean Validation** (première barrière, messages clairs) ; le domaine reste la vraie garantie.
- Le domaine n'utilise pas Bean Validation : vérifications explicites en Java.

## Montants

- **Toujours en centimes d'euro, dans un entier (`long`)**, comme dans les systèmes bancaires.
- Les couches `domain`, `application` et la persistance **ne manipulent jamais de nombre à virgule** : ni `float`, ni `double`, ni `BigDecimal`. En base : colonne `BIGINT` `…_cents`.
- L'API échange aussi des centimes (`amountCents`). Seule la PWA convertit entre la saisie ou l'affichage (« 12,50 € ») et les centimes.

## Événements

- **Événement de domaine** (`domain/event`) : interne, peut contenir des value objects.
- **Événement de contrat** (`contract/event`) : public, `record` à types simples (UUID, String, Instant), stable, sérialisable en JSON. C'est ce qui passera par un broker demain.
- Publication : le use case appelle `DomainEventPublisher` ; le relais `infrastructure/out/event` du domaine traduit les événements de domaine en événements de contrat et les écrit dans la **boîte d'envoi** du domaine (`Outbox`, table `outbox` de son schéma), **avant le commit**, dans la même transaction (`@TransactionalEventListener(BEFORE_COMMIT)`).
- Distribution : dès le commit, le facteur (`kernel.infrastructure.outbox`) publie les événements sur le **bus d'événements Micronaut**, dans l'ordre ; ce qui reste (arrêt brutal, destinataire en erreur) est relancé au démarrage et toutes les 5 s. Après 10 échecs, un événement est mis de côté et journalisé en erreur.
- Un domaine producteur déclare sa table (migration `outbox`), son dépôt qui hérite de `OutboxJdbcRepository` et une fabrique `Outbox` nommée comme lui.
- Les autres domaines écoutent les événements de contrat (`@EventListener` dans `infrastructure/in/event`). Un événement peut arriver **deux fois** : chaque destinataire doit produire le même résultat (ajout « si absent », remplacement en bloc, clé unique…).
- Le passage à un broker externe ne changera que le facteur.

## Base de données

- Une base PostgreSQL `lifemanager`, **un schéma par domaine** (`identity`, `household`…).
- **Une datasource Micronaut par domaine** (même base, schéma différent) et **une configuration Flyway par datasource** (`db/migration/<domaine>`). Découper = changer l'URL.
- **Aucune clé étrangère ni jointure entre schémas.** Une donnée d'un autre domaine est copiée localement à partir de ses événements si on en a besoin.
- Identifiants : UUID. L'identifiant d'un utilisateur, partagé par tous les domaines, est le `sub` Keycloak.

## Fichiers envoyés

- Le contenu des fichiers (pièces jointes du carnet…) n'est **pas en base** : seulement leurs métadonnées. Le contenu est sur le disque du serveur, sous `life-manager.storage.root` (variable `STORAGE_ROOT`), **un sous-dossier par domaine** (`vehicle/`…) : découper un domaine = déplacer son dossier.
- Chaque domaine accède à ses fichiers par un **port out** (ex. `AttachmentStorage`) ; l'adaptateur disque range un fichier sous une clé UUID choisie par le serveur, sans extension ni nom d'origine.
- Le vrai type d'un fichier est vérifié d'après son contenu (Apache Tika, `tika-core`), jamais d'après son nom ou le `Content-Type` du client.
- Cohérence fichier / base : le fichier est écrit **avant** la transaction et supprimé si elle échoue ; à la suppression, la ligne part dans la transaction et le fichier **après le commit** (événement de domaine). Un échec laisse au pire un fichier orphelin, jamais une ligne sans fichier.
- Dev : `backend/data/files` (ignoré par Git). Tests : dossier temporaire. Production : volume Docker inclus dans les sauvegardes.

## Java et style de code

- **Java 25** : `record` pour value objects, commandes, résultats et événements ; `sealed` pour les hiérarchies fermées ; pattern matching ; `Optional` en retour uniquement.
- Pas de Lombok (les records suffisent).
- Mapping agrégat ↔ table écrit à la main (une petite classe `…Mapper` par agrégat) : explicite, et les agrégats gardent des accesseurs modernes (`email()`) que MapStruct ne sait pas lire.
- **Méthodes courtes** (~20 lignes max), **classes courtes** (~200 lignes max), **complexité cyclomatique ≤ 10** : contrôlé par Checkstyle dans le build Maven.
- Noms en **anglais**, explicites, sans abréviation.
- **Commentaires en français**, courts et utiles : une Javadoc d'une ou deux lignes sur chaque classe publique pour dire à quoi elle sert, et un commentaire seulement là où le *pourquoi* n'est pas évident. On ne paraphrase pas le code.

## Librairies retenues

| Besoin | Librairie |
|---|---|
| Framework, DI, HTTP, sécurité | Micronaut 5 |
| Persistance | Micronaut Data JDBC + Flyway |
| Cache | Caffeine (`micronaut-cache-caffeine`) |
| Limitation de débit | Bucket4j |
| Tests | JUnit 5, AssertJ, Testcontainers |
| Règles d'architecture | ArchUnit |
| Qualité / complexité | Checkstyle |

## PWA (frontend)

Svelte 5 (runes), TypeScript, Vite, Bun. Organisation de `frontend/src/lib` :

| Dossier | Contenu |
|---|---|
| `api/` | appels de l'API, un fichier par domaine ; mise en forme des données reçues si besoin (ex. noms de personnes) |
| `ui/atoms/` | briques visuelles sans logique métier : `Chip`, `Skeleton`, `ProgressBar`, `AmountInput`, `DragHandle` |
| `ui/molecules/` | assemblages réutilisables : `Modal`, `ConfirmDialog`, `TextDialog`, `Segmented`, `Fab`, `SpaceTabs` |
| `ui/organisms/` | blocs complets d'écran : `MainNav` |
| `components/<section>/` | écrans et blocs propres à une section (budget, charges, courses…) |
| `*.ts` à la racine | logique pure et testée (`money`, `dates`, `members`, `navigation`, `reorder`, `scroll`…) |

- **Pas de doublon** (Atomic Design) : une logique utilisée deux fois part dans un `lib/*.ts` testé ; un élément visuel utilisé deux fois part dans `ui/`.
- **Navigation par espaces** (`lib/navigation.ts`, seule source) : Argent, Courses, Maison, Foyer. Un nouvel écran rejoint un espace existant en une ligne ; la barre du bas ne grossit pas.
- **Glisser-déposer** : `svelte-dnd-action`, toujours avec une poignée (`DragHandle`) pour ne pas gêner le défilement au doigt ; réglages et pièges (signal tardif au clavier) dans `lib/reorder.ts`.
