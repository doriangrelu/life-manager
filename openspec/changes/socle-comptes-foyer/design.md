## Context

Le dépôt ne contient encore aucun code : ce change pose à la fois le socle fonctionnel (comptes, foyers) et le squelette technique. Choix arrêtés : authentification **Keycloak / OIDC** avec **jetons opaques introspectés**, backend **Micronaut** construit avec **Maven** et compilé en GraalVM native image, **PWA** unique, auto-hébergement sur un VPS OVH. L'architecture suit `docs/architecture.md` : monolithe modulaire en clean architecture, domaines séparés jusque dans la base, communication inter-domaines par événements.

## Goals / Non-Goals

**Goals:**
- Identifier l'utilisateur sur chaque appel API sans gérer de mots de passe ni valider de JWT côté backend.
- Permettre à un utilisateur d'appartenir à plusieurs foyers, avec un cloisonnement réutilisable par les futurs domaines.
- Poser le *kernel*, la plateforme technique et les règles d'architecture vérifiées automatiquement.
- Un backend qui compile et tourne en natif dès le premier change.
- Un parcours bout en bout dans la PWA.

**Non-Goals:**
- Déploiement production, TLS, sauvegardes (change dédié).
- Rôles, départ d'un foyer, retrait de membre, suppression.
- *Outbox* et broker externe.
- Hors-ligne et push dans la PWA.

## Decisions

### 1. Domaines métiers

| Domaine | Package | Schéma | Responsabilité |
|---|---|---|---|
| Identité | `identity` | `identity` | Profil local de l'utilisateur, synchronisé depuis Keycloak |
| Foyer | `household` | `household` | Foyers, appartenance, invitations, annuaire local des membres |

S'y ajoutent deux packages sans métier : `kernel` (briques DDD) et `platform` (sécurité, erreurs HTTP).
L'identifiant d'un utilisateur, commun à tous les domaines, est le **`sub` Keycloak** (UUID) : chaque domaine peut le lire dans l'authentification sans interroger `identity`, ce qui reste vrai après un découpage.

### 2. Kernel

- `AggregateRoot<ID>` : identifiant + liste d'événements enregistrés (`recordEvent`, `pullEvents`).
- `DomainEvent` : interface marqueur avec `occurredAt()`.
- `DomainException` (abstraite) et `InvalidValueException` (forme de donnée invalide).
- Port `DomainEventPublisher` + adaptateur s'appuyant sur `ApplicationEventPublisher` de Micronaut.
Le use case sauvegarde l'agrégat puis publie ses événements via ce port.

### 3. Domaine `identity`

**Agrégat `UserAccount`** — invariant : un profil par utilisateur Keycloak, toujours avec un email et un nom valides.
- Value objects : `UserId` (UUID du `sub`), `EmailAddress`, `DisplayName` (1 à 100 caractères, espaces rognés).
- Méthodes : `register(...)` → `UserRegistered` ; `updateProfile(email, name)` → `UserProfileChanged` seulement si quelque chose change.

**Use cases** : `SyncUserAccount` (crée ou met à jour le profil), `GetCurrentUser` (whoami).
**Événements de contrat** : `UserRegisteredV1`, `UserProfileChangedV1` (`userId`, `email`, `displayName`, `occurredAt`).
**Déclenchement de la synchro** : la plateforme publie un événement technique `UserAuthenticated` (sub, email, nom) quand une introspection réelle a lieu (pas sur un résultat en cache) ; un listener d'`identity` appelle `SyncUserAccount`. L'écoute est synchrone, donc le profil existe avant que la requête atteigne un contrôleur.

### 4. Domaine `household`

**Agrégat `Household`** — invariants : nom valide, au moins un membre, pas de membre en double.
- Value objects : `HouseholdId`, `HouseholdName` (1 à 100 caractères), `MemberId` (UUID du `sub`).
- Entité interne : `Membership` (`MemberId`, date d'adhésion).
- Méthodes : `create(name, founder)` → `HouseholdCreated` ; `addMember(member, at)` → `MemberJoined`.

**Agrégat `Invitation`** — invariants : usage unique, pas d'acceptation après expiration.
- Value objects : `InvitationId`, `InvitationCode` (8 caractères dans `ABCDEFGHJKLMNPQRSTUVWXYZ23456789`), `InvitationStatus` (`PENDING`, `ACCEPTED`, `REVOKED`).
- Méthodes : `issue(household, code, now)` (valable 7 jours) ; `accept(member, now)` → `InvitationAccepted` ; `revoke()`.

Séparer `Invitation` de `Household` garde `Household` petit : les deux n'ont pas d'invariant commun.

**Annuaire des membres** (`MemberProfile` : `MemberId`, nom d'affichage) : copie locale, sans comportement métier, alimentée par les événements de contrat d'`identity`. Ce n'est pas un agrégat : un port out `MemberDirectory` et sa table.

**Use cases** : `CreateHousehold`, `ListMyHouseholds`, `GetHousehold`, `IssueInvitation`, `JoinHousehold`, `UpdateMemberProfile` (appelé par le listener).
**Services applicatifs** :
- `HouseholdAccess` : charge un foyer seulement si l'utilisateur en est membre, sinon `HouseholdNotFoundException` (→ 404). Utilisé par tous les use cases ciblant un foyer.
- `MembershipQuota` : vérifie le plafond de 10 foyers, partagé par `CreateHousehold` et `JoinHousehold`.

**Exception assumée** : `JoinHousehold` modifie deux agrégats (`Invitation` acceptée, `Household` avec un membre de plus) dans **une seule transaction**. Ils sont dans le même domaine et le même schéma, et une acceptation sans adhésion n'aurait pas de sens pour l'utilisateur. `IssueInvitation` révoque l'éventuelle invitation en attente avant d'en créer une nouvelle, dans la même transaction.

### 5. Base de données

Une base PostgreSQL `lifemanager`, deux schémas, **une datasource et une configuration Flyway par domaine**, aucune clé étrangère entre schémas.
- `identity.user_account(id PK = sub, email, display_name, created_at, updated_at)`
- `household.household(id, name, created_at)`
- `household.membership(household_id FK, member_id, joined_at, PK (household_id, member_id))`
- `household.invitation(id, household_id FK, code UNIQUE, status, expires_at, accepted_by, created_at)`
- `household.member_profile(member_id PK, display_name, updated_at)`
Persistance avec Micronaut Data JDBC (moins de réflexion que JPA, adapté au natif) ; entités de table séparées des objets métier, mappers écrits à la main.

### 6. Authentification (`platform.infrastructure.security`)

- Jeton traité comme **opaque** : jamais décodé, vérifié par l'endpoint d'**introspection** Keycloak (RFC 7662) avec le client confidentiel `life-manager-api` ; accepté si `active: true` et audience correcte.
- `TokenValidator` maison (Micronaut n'offre pas l'introspection côté resource server) ; cache Caffeine de 30 s, clé = SHA-256 du jeton.
- *Lightweight access token* activé sur `life-manager-pwa` ; les claims utiles sont fournis seulement dans la réponse d'introspection.
- 401 si jeton absent ou inactif, 503 si Keycloak est injoignable.
- `whoami` maison : Keycloak n'accepte pas `userinfo` avec ce mode de jetons.

### 7. API REST (préfixe `/api`)

| Route | Domaine |
|---|---|
| `GET /api/whoami` | identity |
| `GET /api/households` / `POST /api/households` | household |
| `GET /api/households/{householdId}` | household |
| `POST /api/households/{householdId}/invitations` | household |
| `POST /api/households/join` | household |
| `GET /health` (public) | platform |

Limitation des tentatives sur `/join` : Bucket4j, 10 essais par utilisateur et par heure.

### 8. Qualité

- Tests ArchUnit pour les règles de dépendance de `docs/architecture.md`.
- Checkstyle dans le build Maven : longueur de méthode et de classe, complexité cyclomatique ≤ 10.
- Tests unitaires du domaine sans framework ; tests d'intégration avec Testcontainers (PostgreSQL) et un faux endpoint d'introspection.

### 9. PWA

Vite + TypeScript + Svelte, `oidc-client-ts`, `vite-plugin-pwa`. Le jeton est gardé en mémoire/sessionStorage, renouvelé par refresh token, jamais décodé. Au démarrage : `whoami` puis `GET /api/households`.

### 10. Structure du dépôt

`backend/` (Maven wrapper, Java 25, Micronaut 5.2, build natif `./mvnw package -Dpackaging=native-image -Dmicronaut.aot.enabled=true` — l'AOT Micronaut est nécessaire en natif), `frontend/`, `infra/keycloak/realm-life-manager.json`, `infra/postgres/init/` (schémas et un compte PostgreSQL par domaine), `docker-compose.yml`, `.devcontainer/` (outillage de dev complet : GraalVM 25, Bun, Docker).

## Risks / Trade-offs

- **Un appel Keycloak par requête hors cache** → latence et dépendance forte ; cache 30 s, services sur la même machine.
- **Délai de révocation = TTL du cache** (30 s) → acceptable.
- **Événements perdus sans outbox** (arrêt entre commit et publication) → un nom de membre peut rester périmé jusqu'à la prochaine modification du profil ; risque accepté à cette échelle, outbox à prévoir avec le broker.
- **Deux datasources** → deux pools de connexions ; tailles réduites (2 à 5).
- **Empreinte de Keycloak** (~500 Mo de RAM) → à surveiller sur le VPS.
- **Natif et réflexion** (sérialisation, client HTTP) → build natif en CI dès ce change.
- **Build natif gourmand** (~7 Go de RAM au pic) → à lancer en CI ou sur le poste, pas sur le VPS.

## Migration Plan

Projet vierge : chaque domaine a sa migration Flyway `V1__…` dans son schéma.

## Open Questions

- Activer la 2FA (TOTP) dans le realm dès maintenant ou plus tard ?
- Sous-domaine dédié pour Keycloak en production — à trancher dans le change de déploiement.
- Le plafond de 10 foyers par utilisateur est-il pertinent ?
