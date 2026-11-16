## 1. Infrastructure de développement

- [x] 1.1 [infra] `docker-compose.yml` : PostgreSQL (base `lifemanager` avec schémas `identity` et `household`, base `keycloak`) et Keycloak en mode dev
- [x] 1.2 [infra] Realm `life-manager` : client public `life-manager-pwa` (PKCE, lightweight access token), client confidentiel `life-manager-api` (introspection), comptes créés par l'admin (pas d'auto-inscription), mappers `email`/`name` ajoutés à l'introspection
- [x] 1.3 [infra] Export du realm dans `infra/keycloak/realm-life-manager.json`, importé au démarrage ; lancement documenté dans le `README.md`
- [x] 1.4 [infra] Devcontainer (GraalVM 25 avec `native-image`, Bun, accès Docker pour Testcontainers, stack lancée sur l'hôte)

## 2. Squelette backend et qualité

- [x] 2.1 [backend] Générer `backend/` (Maven wrapper, Java 25, Micronaut 5.2) avec security, http-client, data-jdbc, flyway, postgres, serde, management, cache-caffeine
- [x] 2.2 [backend] Checkstyle (longueur méthode/classe, complexité ≤ 10) branché sur `./mvnw verify`
- [x] 2.3 [backend] Tests ArchUnit des règles de dépendance de `docs/architecture.md` (domain pur, contrôleurs → ports in, domaines isolés sauf `contract`)
- [x] 2.4 [backend] Vérifier que `./mvnw package -Dpackaging=native-image -Dmicronaut.aot.enabled=true` produit un binaire qui démarre et répond sur `/health`

## 3. Kernel

- [x] 3.1 [kernel/domain] `AggregateRoot`, `DomainEvent`, `DomainException`, `InvalidValueException` + tests unitaires
- [x] 3.2 [kernel/application + infrastructure] Port `DomainEventPublisher` et adaptateur sur le bus Micronaut + test

## 4. Plateforme : sécurité et erreurs

- [x] 4.1 [platform/security] `TokenValidator` par introspection Keycloak (client confidentiel, `active` + audience, 401 / 503) ; aucune dépendance `security-jwt`
- [x] 4.2 [platform/security] Cache d'introspection Caffeine (30 s, clé SHA-256) et publication de `UserAuthenticated` sur introspection réelle + tests
- [x] 4.3 [platform/security] Faux endpoint d'introspection et support Testcontainers pour les tests d'intégration
- [x] 4.4 [platform/web] Traduction des exceptions (`InvalidValueException` → 400, `…NotFoundException` → 404, conflits → 409) en réponses JSON homogènes

## 5. Domaine identity

- [x] 5.1 [identity/domain] Value objects `UserId`, `EmailAddress`, `DisplayName` et agrégat `UserAccount` avec ses événements + tests unitaires
- [x] 5.2 [identity/infrastructure/out] Datasource et Flyway du schéma `identity`, table `user_account`, repository, mapper, adaptateur du port out
- [x] 5.3 [identity/application] Use case `SyncUserAccount` + listener de `UserAuthenticated` (infrastructure/in/event) + tests
- [x] 5.4 [identity/infrastructure/out/event] Traduction en `UserRegisteredV1` / `UserProfileChangedV1` (contract) et publication + test
- [x] 5.5 [identity] Use case `GetCurrentUser` et `GET /api/whoami` + tests (sans jeton → 401, jeton inactif → 401, profil créé au premier appel)

## 6. Domaine household

- [x] 6.1 [household/domain] Value objects `HouseholdId`, `HouseholdName`, `MemberId`, entité `Membership`, agrégat `Household` + tests unitaires
- [x] 6.2 [household/domain] Value objects `InvitationId`, `InvitationCode`, `InvitationStatus`, agrégat `Invitation` + tests unitaires (expiration, usage unique, révocation)
- [x] 6.3 [household/infrastructure/out] Datasource et Flyway du schéma `household`, tables, repositories, mappers, adaptateurs
- [x] 6.4 [household/application] Services `HouseholdAccess` et `MembershipQuota` + tests
- [x] 6.5 [household] `CreateHousehold`, `ListMyHouseholds` et leurs routes + tests
- [x] 6.6 [household] `GetHousehold` (membres avec noms de l'annuaire, code actif) et sa route + tests, dont non-membre → 404
- [x] 6.7 [household] `IssueInvitation` (révocation de la précédente, code `SecureRandom`) et sa route + tests
- [x] 6.8 [household] `JoinHousehold` (transaction unique, erreurs 400/409) et sa route + tests
- [x] 6.9 [household/infrastructure/in/web] Limitation Bucket4j sur `/join` + test
- [x] 6.10 [household/infrastructure/in/event] Listener des événements de contrat `identity` → `UpdateMemberProfile` (annuaire) + test
- [x] 6.11 [household] Test de cloisonnement : un membre du seul foyer A ne voit rien du foyer B

## 7. PWA minimale

- [x] 7.1 [frontend] Initialiser `frontend/` (Vite, TypeScript, Svelte, `vite-plugin-pwa`) et le proxy `/api`
- [x] 7.2 [frontend] `oidc-client-ts` : connexion PKCE, callback, renouvellement, déconnexion ; client HTTP ajoutant le jeton sans le décoder
- [x] 7.3 [frontend] Chargement de `whoami` puis des foyers, sélecteur de foyer mémorisé sur l'appareil
- [x] 7.4 [frontend] Écran « Créer ou rejoindre un foyer »
- [x] 7.5 [frontend] Écran du foyer (membres, génération et copie du code)
- [ ] 7.6 [frontend] Vérification manuelle à deux comptes et deux foyers, desktop et mobile

## 8. Intégration continue

- [ ] 8.1 [ci] GitHub Actions : `./mvnw verify` (tests, Checkstyle, ArchUnit), build natif, build de la PWA
