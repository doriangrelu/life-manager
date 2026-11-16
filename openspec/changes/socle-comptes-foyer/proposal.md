## Why

Toutes les fonctionnalités du MVP (budget, véhicules, courses) manipulent des données partagées au sein d'un foyer. Avant de construire le moindre domaine, il faut savoir **qui** est connecté et **à quels foyers** il a accès. C'est aussi l'occasion de poser le squelette technique (backend Micronaut natif, Keycloak, PWA) sur lequel tout le reste s'appuiera.

## What Changes

- Mise en place d'un **Keycloak** (realm `life-manager`) comme fournisseur d'identité OIDC : inscription, connexion, mot de passe oublié sont délégués à Keycloak.
- Création du **backend Micronaut** (Maven, compilable en GraalVM native image) : PostgreSQL, migrations Flyway.
- **Jetons traités comme opaques** : le backend ne décode ni ne valide aucun JWT ; chaque jeton est vérifié par **introspection** auprès de Keycloak.
- **Endpoint `whoami` maison** : l'identité de l'utilisateur est servie par le backend à partir de l'introspection (le `userinfo` de Keycloak n'est pas utilisé).
- Provisionnement automatique d'un **profil utilisateur local** au premier appel authentifié.
- **Foyers multiples** : un utilisateur peut créer plusieurs foyers et être membre de plusieurs ; invitation par code à usage unique ; consultation des membres.
- **Cloisonnement par appartenance** : toute ressource d'un foyer dont l'utilisateur n'est pas membre est inaccessible.
- **PWA minimale** : connexion OIDC (Authorization Code + PKCE), liste et sélection du foyer, création / adhésion, écran du foyer.
- Environnement de développement `docker compose` (PostgreSQL + Keycloak).

## Capabilities

### New Capabilities

- `comptes-utilisateurs` : authentification par jeton opaque introspecté, profil utilisateur local, endpoint `whoami`.
- `foyer` : création de foyers, invitation et adhésion, liste des membres, cloisonnement des données par foyer.

### Modified Capabilities

_Aucune (projet vierge)._

## Impact

- Nouveau code : `backend/` (Micronaut, Java 21, Maven), `frontend/` (PWA), `docker-compose.yml`, export du realm Keycloak.
- Nouvelles dépendances d'infrastructure : PostgreSQL, Keycloak (à déployer à terme sur le VPS OVH).
- Tous les changes suivants s'appuieront sur l'utilisateur courant et sur un foyer explicitement ciblé.

## Hors périmètre

- Rappels / notifications (pourtant listés dans le socle de la vision) : change dédié.
- Quitter un foyer, retirer un membre, supprimer un foyer ou un compte.
- Rôles ou permissions différenciés entre membres (tous les membres sont égaux).
- Déploiement en production sur le VPS, sauvegardes, TLS : change dédié.
- Mode hors-ligne de la PWA et notifications push.
