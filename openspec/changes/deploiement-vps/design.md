## Context

Aujourd'hui : un `docker-compose.yml` de développement (PostgreSQL 18, Keycloak 26.4 en `start-dev`), un backend Micronaut compilé en natif par la CI (job « Build natif GraalVM », sans publication d'image), une PWA Vite (`dist/`), un realm Keycloak de dev (redirections vers `localhost:5173`, secrets de dev), un script d'init PostgreSQL qui crée un schéma et un compte par domaine (`identity`, `household`, `budget`, `vehicle`, `shopping`, `reminder`). Aucun serveur n'est encore configuré. Ce change est une **proposition** : rien n'est déployé ni installé.

## Goals / Non-Goals

**Goals:**
- Une installation reproductible sur un VPS Debian/Ubuntu avec Docker, en une poignée de commandes.
- Sécurité de base sérieuse pour des données financières : HTTPS, secrets uniques, base non exposée, sauvegardes chiffrées hors site.
- Peu de ressources : backend natif, un seul PostgreSQL partagé (schémas séparés), pas d'outillage lourd.

**Non-Goals:**
- Orchestration (Kubernetes), haute disponibilité, CD automatique.

## Decisions

### 1. Docker Compose sur le VPS

`docker-compose.prod.yml` : `caddy`, `api` (natif), `postgres`, `keycloak` (`start`, mode production, base PostgreSQL), `backup`. Réseau interne Docker ; seul Caddy publie 80/443. Volumes nommés pour PostgreSQL et les certificats Caddy. Politique `restart: unless-stopped`, contrôles de santé.

### 2. Caddy en frontal

Un domaine pour l'application (`app.<domaine>`) : Caddy sert les fichiers de la PWA et relaie `/api/*` vers le backend. Un sous-domaine `auth.<domaine>` relaie vers Keycloak (`KC_PROXY_HEADERS=xforwarded`, `KC_HOSTNAME` fixé). Certificats Let's Encrypt automatiques, en-têtes de sécurité (HSTS, `X-Content-Type-Options`, CSP restreinte au domaine et à `auth.<domaine>`). La console d'administration Keycloak n'est accessible que par tunnel SSH.

### 3. Images construites par la CI

Workflow déclenché sur tag `v*` : build natif du backend (image distroless ou `debian-slim` minimale, utilisateur non root), build de la PWA avec `VITE_OIDC_AUTHORITY` de production, publication sur `ghcr.io/doriangrelu/life-manager-api` et `…-web` avec le tag. Les images ne contiennent aucun secret.

### 4. Secrets et configuration

`infra/deploy/install.sh` (à lancer sur le VPS) : vérifie Docker, crée `/opt/life-manager`, génère `.env` (mots de passe aléatoires par compte PostgreSQL, secret du client API, admin Keycloak, mot de passe restic) avec droits `600`, copie le compose et le Caddyfile. Le realm de production (`realm-life-manager.prod.json`) reprend celui de dev avec les URL du domaine, l'inscription fermée, et un secret client lu depuis l'environnement à l'import.

### 5. Sauvegardes

Conteneur `backup` (image officielle restic + client PostgreSQL) déclenché chaque nuit : `pg_dump` de `lifemanager` et `keycloak`, envoi chiffré vers un dépôt restic externe (OVH Object Storage compatible S3), puis `restic forget --keep-daily 7 --keep-weekly 4 --keep-monthly 6 --prune`. Restauration documentée et testée une fois sur une machine vierge.

### 6. Mise à jour

`infra/deploy/deploy.sh <tag>` : met à jour la variable de tag dans `.env`, `docker compose pull`, `docker compose up -d`, attend `GET /health = UP`. Retour arrière = relancer avec le tag précédent (les migrations Flyway sont additives ; une migration destructive demandera une sauvegarde préalable explicite).

### 7. Durcissement du VPS (documenté, appliqué par Dorian)

SSH par clé uniquement, connexion root désactivée, pare-feu (22, 80, 443), `unattended-upgrades`, fail2ban facultatif.

## Risks / Trade-offs

- **Un seul serveur** → indisponibilité possible lors d'une panne OVH ; acceptable pour un usage familial, la sauvegarde hors site garantit les données.
- **Build natif en CI (~7 Go de RAM, plusieurs minutes)** → limité aux tags.
- **Keycloak consomme plus que le backend** (~400–600 Mo) → dimensionner le VPS à 2 Go de RAM minimum.
- **Sans outbox**, un arrêt brutal pendant une publication d'événement peut désynchroniser une copie locale (membres, échéances) ; risque inchangé, à traiter avec un broker.

## Migration Plan

1. Créer les DNS `app.` et `auth.` vers le VPS. 2. Durcir le VPS. 3. `install.sh`. 4. Premier `deploy.sh v0.1.0`. 5. Créer les deux comptes dans Keycloak. 6. Vérifier une sauvegarde puis une restauration. Retour arrière : arrêter la pile, aucune donnée locale à migrer.

## Open Questions

- Nom de domaine et fournisseur DNS de Dorian.
- Stockage des sauvegardes : OVH Object Storage, autre fournisseur, ou machine personnelle via SFTP ?
- Taille du VPS déjà souscrit (RAM disponible pour Keycloak).

## Décisions prises sans Dorian

1. **Docker Compose plutôt que Kubernetes ou installation directe (systemd)** — alternatives : k3s, binaires natifs gérés par systemd. Raison : déjà utilisé en dev, reproductible, adapté à un seul serveur. Revenir en arrière : les images restent utilisables par tout orchestrateur.
2. **Caddy comme reverse proxy** — alternatives : Nginx + certbot, Traefik. Raison : HTTPS automatique sans configuration, fichier de configuration court. Revenir en arrière : remplacer le service `caddy` par Nginx, les autres services ne changent pas.
3. **Deux sous-domaines (`app.` et `auth.`)** — alternative : Keycloak sous un chemin `/auth` du même domaine. Raison : configuration Keycloak plus simple et cookies isolés. Revenir en arrière : `KC_HTTP_RELATIVE_PATH=/auth` et une route Caddy.
4. **Images publiées sur GHCR par la CI sur tag** — alternatives : build sur le VPS, Docker Hub. Raison : le build natif est trop gourmand pour un petit VPS ; GHCR est intégré au dépôt GitHub. Revenir en arrière : build local puis `docker save | ssh … docker load`.
5. **Déploiement déclenché manuellement par Dorian (`deploy.sh`)** — alternative : CD par SSH depuis GitHub Actions. Raison : pas de clé SSH du serveur dans la CI, contrôle humain sur un projet perso. Revenir en arrière : ajouter un job `workflow_dispatch` qui lance `deploy.sh` par SSH.
6. **restic vers un stockage objet externe pour les sauvegardes** — alternatives : simple `pg_dump` local, snapshots OVH. Raison : chiffrement, déduplication, rétention intégrée et données hors du serveur. Revenir en arrière : remplacer la cible restic (SFTP, disque local) sans changer la procédure.
7. **Un seul PostgreSQL pour l'application et Keycloak** (bases distinctes, comptes distincts) — alternative : une instance par usage. Raison : économie de mémoire ; l'isolation par comptes et schémas est déjà en place. Revenir en arrière : sortir Keycloak dans sa propre instance.
