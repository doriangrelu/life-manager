## Context

Le dépôt ne contient encore aucun code : ce change pose à la fois le socle fonctionnel (comptes, foyers) et le squelette technique. Choix arrêtés : authentification **Keycloak / OIDC** avec **jetons opaques introspectés**, backend **Micronaut** construit avec **Maven** et compilé en GraalVM native image, **PWA** unique pour mobile et desktop, auto-hébergement sur un VPS OVH. Deux utilisateurs visés au départ, mais un utilisateur doit pouvoir gérer plusieurs foyers (ex. résidence principale et maison de famille).

## Goals / Non-Goals

**Goals:**
- Identifier l'utilisateur sur chaque appel API sans gérer de mots de passe ni valider de JWT côté backend.
- Permettre à un utilisateur d'appartenir à plusieurs foyers et fournir un cloisonnement réutilisable par les futurs domaines.
- Avoir un backend qui compile et tourne en natif dès le premier change.
- Un parcours bout en bout dans la PWA : connexion → créer/rejoindre un foyer → choisir un foyer → le consulter.

**Non-Goals:**
- Déploiement production, TLS, sauvegardes (change dédié).
- Rôles, départ d'un foyer, retrait de membre, suppression.
- Hors-ligne et push dans la PWA.

## Decisions

**1. Keycloak comme IdP, jetons traités comme opaques.**
Keycloak porte inscription, connexion, reset de mot de passe. Le backend considère le jeton d'accès comme une chaîne opaque : aucun décodage, aucune vérification de signature locale. Chaque jeton est vérifié via l'endpoint d'**introspection** de Keycloak (RFC 7662), en s'authentifiant avec un client confidentiel `life-manager-api` (secret fourni par variable d'environnement). Un jeton est accepté seulement si la réponse contient `active: true` et l'audience attendue.
L'option *lightweight access token* est activée sur le client `life-manager-pwa` pour que le jeton transporté par le navigateur ne contienne pas de données personnelles ; les claims utiles (`sub`, `email`, `name`, `preferred_username`) sont exposés uniquement dans la réponse d'introspection (mappers « Add to token introspection »).
Alternative écartée : validation locale des JWT via JWKS — plus performante mais refusée (pas de JWT validé côté backend, révocation immédiate souhaitée).

**2. Intégration Micronaut.**
Micronaut Security n'offre pas d'introspection prête à l'emploi côté resource server : on implémente un `TokenValidator` qui appelle l'introspection via le client HTTP Micronaut et construit l'`Authentication` à partir de la réponse. Le lecteur de jeton `Authorization: Bearer` standard est conservé. Les réponses actives sont mises en cache 30 s (clé : empreinte SHA-256 du jeton, jamais le jeton en clair) pour limiter la charge sur Keycloak.

**3. `whoami` maison, pas de `userinfo`.**
Keycloak refuse l'appel à `userinfo` avec ce mode de jetons ; la PWA obtient donc l'identité via `GET /api/whoami`, construit par le backend à partir du profil local. La PWA ne lit jamais le contenu du jeton.

**4. Profil local provisionné à la volée (JIT).**
Lors de la construction de l'`Authentication`, le backend crée ou met à jour `user_account` à partir des claims d'introspection. Le `sub` est la clé de rattachement ; l'`id` interne (UUID) est utilisé dans toutes les relations.

**5. Modèle de données (PostgreSQL, Flyway, Micronaut Data JDBC).**
- `user_account(id, keycloak_sub UNIQUE, email, display_name, created_at)`
- `household(id, name, created_by, created_at)`
- `household_member(household_id, user_id, joined_at, PRIMARY KEY (household_id, user_id))` — relation n-n : un utilisateur peut être dans plusieurs foyers.
- `household_invitation(id, household_id, code UNIQUE, expires_at, used_at, used_by, created_by, created_at)`
Micronaut Data JDBC plutôt que JPA : moins de réflexion, meilleur comportement en native image.

**6. Foyer ciblé explicitement + contrôle d'appartenance.**
Avec plusieurs foyers par utilisateur, il n'y a plus de « foyer courant » implicite côté serveur : chaque route d'un foyer porte son identifiant (`/api/households/{householdId}/…`). Un composant `HouseholdAccess` vérifie l'appartenance de l'utilisateur authentifié avant tout traitement ; un foyer dont il n'est pas membre est traité comme inexistant (`404`). Les futurs domaines suivront le même schéma d'URL et filtreront par `household_id` vérifié. Le dernier foyer sélectionné est mémorisé côté PWA uniquement (préférence d'affichage).
Limite : 10 foyers maximum par utilisateur, pour borner les abus sur une instance auto-hébergée.

**7. Code d'invitation.**
8 caractères dans un alphabet sans ambiguïté (sans 0/O/1/I), généré par `SecureRandom`, stocké en clair car l'écran du foyer réaffiche le code actif. Valable 7 jours, un seul code actif par foyer : en générer un nouveau fait expirer le précédent. Consommation transactionnelle (`UPDATE … WHERE used_at IS NULL AND expires_at > now()`).

**8. API REST** (préfixe `/api`) :
- `GET /api/whoami` — profil + liste des foyers (id, nom)
- `GET /api/households` — foyers de l'utilisateur
- `POST /api/households` — créer
- `GET /api/households/{householdId}` — détail et membres
- `POST /api/households/{householdId}/invitations` — générer un code
- `POST /api/households/join` — rejoindre avec `{ code }`
- `GET /health` — public

**9. PWA : Vite + TypeScript + Svelte, `oidc-client-ts`, `vite-plugin-pwa`.**
Hypothèse retenue faute de préférence exprimée. Le jeton est conservé en mémoire/sessionStorage et renouvelé par refresh token ; il n'est jamais décodé côté client.

**10. Structure du dépôt.**
`backend/` (Maven wrapper, Java 21, Micronaut 4, build natif via `./mvnw package -Dpackaging=native-image`), `frontend/`, `infra/keycloak/realm-life-manager.json`, `docker-compose.yml` à la racine.

## Risks / Trade-offs

- **Un appel Keycloak par requête** (hors cache) → latence et dépendance forte à Keycloak. Mitigation : cache court ; Keycloak et le backend sur la même machine.
- **Délai de révocation égal au TTL du cache** (30 s) → acceptable pour l'usage.
- **Empreinte de Keycloak** (JVM, ~500 Mo de RAM) → contredit en partie la sobriété du backend natif. Mitigation : surveiller sur le VPS ; base PostgreSQL partagée (base séparée).
- **Native image et réflexion** (sérialisation, client HTTP) → build natif en CI dès ce change.
- **Force brute sur `/join`** (≈ 10¹² combinaisons) → limitation du nombre de tentatives par utilisateur.

## Migration Plan

Projet vierge : pas de migration. La migration Flyway `V1__…` crée le schéma initial.

## Open Questions

- Activer la 2FA (TOTP) dans le realm dès maintenant ou plus tard ?
- Sous-domaine dédié pour Keycloak en production — à trancher dans le change de déploiement.
- Le plafond de 10 foyers par utilisateur est-il pertinent ?
