## 1. Infrastructure de développement

- [ ] 1.1 [infra] `docker-compose.yml` : PostgreSQL (base `lifemanager` avec schémas `identity` et `household`, base `keycloak`) et Keycloak en mode dev
- [ ] 1.2 [infra] Realm `life-manager` : client public `life-manager-pwa` (PKCE, lightweight access token), client confidentiel `life-manager-api` (introspection), auto-inscription, mappers `email`/`name` ajoutés à l'introspection
- [ ] 1.3 [infra] Export du realm dans `infra/keycloak/realm-life-manager.json`, importé au démarrage ; lancement documenté dans le `README.md`

## 2. Squelette backend et qualité

- [ ] 2.1 [backend] Générer `backend/` (Maven wrapper, Java 25 — repli Java 21 après accord si incompatible, Micronaut 4) avec security, http-client, data-jdbc, flyway, postgres, serde, management, cache-caffeine, MapStruct
- [ ] 2.2 [backend] Checkstyle (longueur méthode/classe, complexité ≤ 10) branché sur `./mvnw verify`
- [ ] 2.3 [backend] Tests ArchUnit des règles de dépendance de `docs/architecture.md` (domain pur, contrôleurs → ports in, domaines isolés sauf `contract`)
- [ ] 2.4 [backend] Vérifier que `./mvnw package -Dpackaging=native-image` produit un binaire qui démarre et répond sur `/health`

## 3. Kernel

- [ ] 3.1 [kernel/domain] `AggregateRoot`, `DomainEvent`, `DomainException`, `InvalidValueException` + tests unitaires
- [ ] 3.2 [kernel/application + infrastructure] Port `DomainEventPublisher` et adaptateur sur le bus Micronaut + test

## 4. Plateforme : sécurité et erreurs

- [ ] 4.1 [platform/security] `TokenValidator` par introspection Keycloak (client confidentiel, `active` + audience, 401 / 503) ; aucune dépendance `security-jwt`
- [ ] 4.2 [platform/security] Cache d'introspection Caffeine (30 s, clé SHA-256) et publication de `UserAuthenticated` sur introspection réelle + tests
- [ ] 4.3 [platform/security] Faux endpoint d'introspection et support Testcontainers pour les tests d'intégration
- [ ] 4.4 [platform/web] Traduction des exceptions (`InvalidValueException` → 400, `…NotFoundException` → 404, conflits → 409) en réponses JSON homogènes

## 5. Domaine identity

- [ ] 5.1 [identity/domain] Value objects `UserId`, `EmailAddress`, `DisplayName` et agrégat `UserAccount` avec ses événements + tests unitaires
- [ ] 5.2 [identity/infrastructure/out] Datasource et Flyway du schéma `identity`, table `user_account`, repository, mapper, adaptateur du port out
- [ ] 5.3 [identity/application] Use case `SyncUserAccount` + listener de `UserAuthenticated` (infrastructure/in/event) + tests
- [ ] 5.4 [identity/infrastructure/out/event] Traduction en `UserRegisteredV1` / `UserProfileChangedV1` (contract) et publication + test
- [ ] 5.5 [identity] Use case `GetCurrentUser` et `GET /api/whoami` + tests (sans jeton → 401, jeton inactif → 401, profil créé au premier appel)

## 6. Domaine household

- [ ] 6.1 [household/domain] Value objects `HouseholdId`, `HouseholdName`, `MemberId`, entité `Membership`, agrégat `Household` + tests unitaires
- [ ] 6.2 [household/domain] Value objects `InvitationId`, `InvitationCode`, `InvitationStatus`, agrégat `Invitation` + tests unitaires (expiration, usage unique, révocation)
- [ ] 6.3 [household/infrastructure/out] Datasource et Flyway du schéma `household`, tables, repositories, mappers, adaptateurs
- [ ] 6.4 [household/application] Services `HouseholdAccess` et `MembershipQuota` + tests
- [ ] 6.5 [household] `CreateHousehold`, `ListMyHouseholds` et leurs routes + tests
- [ ] 6.6 [household] `GetHousehold` (membres avec noms de l'annuaire, code actif) et sa route + tests, dont non-membre → 404
- [ ] 6.7 [household] `IssueInvitation` (révocation de la précédente, code `SecureRandom`) et sa route + tests
- [ ] 6.8 [household] `JoinHousehold` (transaction unique, erreurs 400/409) et sa route + tests
- [ ] 6.9 [household/infrastructure/in/web] Limitation Bucket4j sur `/join` + test
- [ ] 6.10 [household/infrastructure/in/event] Listener des événements de contrat `identity` → `UpdateMemberProfile` (annuaire) + test
- [ ] 6.11 [household] Test de cloisonnement : un membre du seul foyer A ne voit rien du foyer B

## 7. PWA minimale

- [ ] 7.1 [frontend] Initialiser `frontend/` (Vite, TypeScript, Svelte, `vite-plugin-pwa`) et le proxy `/api`
- [ ] 7.2 [frontend] `oidc-client-ts` : connexion PKCE, callback, renouvellement, déconnexion ; client HTTP ajoutant le jeton sans le décoder
- [ ] 7.3 [frontend] Chargement de `whoami` puis des foyers, sélecteur de foyer mémorisé sur l'appareil
- [ ] 7.4 [frontend] Écran « Créer ou rejoindre un foyer »
- [ ] 7.5 [frontend] Écran du foyer (membres, génération et copie du code)
- [ ] 7.6 [frontend] Vérification manuelle à deux comptes et deux foyers, desktop et mobile

## 8. Intégration continue

- [ ] 8.1 [ci] GitHub Actions : `./mvnw verify` (tests, Checkstyle, ArchUnit), build natif, build de la PWA
