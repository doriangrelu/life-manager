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
| Foyer | `household` | Foyers, membres, invitations |
| _à venir_ | `budget`, `vehicle`, `shopping`, `reminder` | Un par domaine du MVP |

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
    │   ├── port/in                 # interface de chaque use case + ses command/result
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

## Use cases et services applicatifs

- **Un use case = une seule action** (`CreateHousehold`, `JoinHousehold`…). Interface dans `port/in`, implémentation `…UseCase` dans `usecase`.
- Un use case peut être `@Transactional`. Il ne modifie qu'**un seul domaine**.
- Entrées/sorties : des `record` (`…Command`, `…Result`) déclarés dans `port/in`.
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

## Événements

- **Événement de domaine** (`domain/event`) : interne, peut contenir des value objects.
- **Événement de contrat** (`contract/event`) : public, `record` à types simples (UUID, String, Instant), stable, sérialisable en JSON. C'est ce qui passera par un broker demain.
- Publication : le use case appelle `DomainEventPublisher` ; l'adaptateur `infrastructure/out/event` du domaine traduit les événements de domaine en événements de contrat et les publie sur le **bus d'événements Micronaut**.
- Les autres domaines écoutent les événements de contrat **après commit** (`@TransactionalEventListener`).
- Limite connue : sans *outbox*, un événement peut être perdu si l'application s'arrête entre le commit et la publication. À traiter au passage à un broker.

## Base de données

- Une base PostgreSQL `lifemanager`, **un schéma par domaine** (`identity`, `household`…).
- **Une datasource Micronaut par domaine** (même base, schéma différent) et **une configuration Flyway par datasource** (`db/migration/<domaine>`). Découper = changer l'URL.
- **Aucune clé étrangère ni jointure entre schémas.** Une donnée d'un autre domaine est copiée localement à partir de ses événements si on en a besoin.
- Identifiants : UUID. L'identifiant d'un utilisateur, partagé par tous les domaines, est le `sub` Keycloak.

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
