## Context

Côté dépôt : un `docker-compose.yml` de développement (PostgreSQL 18, Keycloak 26.4 en `start-dev`), un backend Micronaut compilé en natif par la CI (sans publication d'image), une PWA Vite (`dist/`), un realm Keycloak de dev, un script d'init PostgreSQL qui crée un schéma et un compte par domaine (`identity`, `household`, `budget`, `vehicle`, `shopping`, `reminder`). Le backend lit sa configuration dans l'environnement (`KEYCLOAK_URL`, `KEYCLOAK_API_CLIENT_*`, `<DOMAINE>_DB_URL/USER/PASSWORD`, `STORAGE_ROOT`).

Côté VPS (inspecté le 2026-11-18, OVH VPS-1, Strasbourg, Ubuntu 26.04, 4 Go de RAM dont 1,7 Go libres, 38 Go de disque dont 11 Go libres) : un **socle partagé** déjà en service pour d'autres applications.

- `~/infra` : Caddy 2 (seul à publier 80/443, routage importé depuis `~/infra/conf.d/*.caddy`) et PostgreSQL 16 (port lié à `127.0.0.1` uniquement), données en bind mount sous `~/volumes`.
- `~/keycloak` : Keycloak `latest` en mode production sur `https://iabilis.fr`, base `keycloak` dans le PostgreSQL du socle, mis à jour automatiquement chaque nuit avec retour arrière.
- Réseau Docker externe `socle` partagé par tous les projets Compose.
- `~/infra/backup.sh` (cron 3 h) : `pg_dumpall` de toutes les bases + volumes + configuration, archive locale gardée 14 jours.
- Serveur : ufw (22/80/443), fail2ban, `unattended-upgrades`, sudo sans mot de passe pour `ubuntu`.

Ce change est une **proposition** : rien n'est déployé ni modifié sur le serveur.

## Goals / Non-Goals

**Goals:**
- Brancher Life Manager sur le socle sans arrêter ni modifier les autres applications.
- Sécurité sérieuse pour des données financières : HTTPS, secrets uniques hors du dépôt, base non exposée, sauvegardes.
- Peu de ressources : backend natif, pas de deuxième PostgreSQL, Keycloak ou Caddy.

**Non-Goals:**
- Orchestration, haute disponibilité, CD automatique.
- Refonte du socle (il appartient à l'organisation décrite dans le dépôt mybook, ADR-0030).

## Decisions

### 1. Une pile Life Manager raccordée au socle

`~/life-manager/docker-compose.prod.yml` : deux services sur le réseau externe `socle`, sans port publié, `restart: unless-stopped`, limites de ressources, contrôles de santé.

- `api` : backend natif (limite 256 Mo), `STORAGE_ROOT=/data/files` monté sur `~/volumes/life-manager-files` (bind mount, comme les autres applications).
- `web` : fichiers statiques de la PWA servis par un petit serveur (limite 32 Mo).

Noms de conteneurs préfixés (`life-manager-api`, `life-manager-web`) pour éviter toute collision d'alias sur le réseau partagé (`app` y est déjà pris par mybook).

### 2. Routage par un fragment Caddy

`infra/caddy/life-manager.caddy`, copié par `deploy.sh` dans `~/infra/conf.d/`, puis `caddy reload` dans le conteneur du socle (aucun redémarrage). Un seul sous-domaine du domaine personnel de Dorian : `maison.dorian-grelu.fr` (domaine acheté le 2026-11-18 chez OVH) : `/api/*` vers `life-manager-api`, le reste vers `life-manager-web`. En-têtes : HSTS, `X-Content-Type-Options`, `Referrer-Policy`, CSP limitée au domaine de l'application et à `https://iabilis.fr` pour la connexion (`connect-src`), avec `blob:` autorisé pour l'affichage des pièces jointes. Caddy obtient le certificat automatiquement dès que le DNS pointe vers le VPS.

### 3. Realm dans le Keycloak existant

Pas de nouveau Keycloak. `infra/keycloak/realm-life-manager.prod.json` reprend le realm de dev : URL de redirection du sous-domaine, inscription fermée, secret du client `life-manager-api` remplacé à l'import par celui du `.env`. Import une fois par `infra/deploy/create-realm.sh`, qui passe par `kcadm.sh` dans le conteneur Keycloak (aucun redémarrage). Émetteur des jetons : `https://iabilis.fr/realms/life-manager`. L'application et Keycloak sont sur deux domaines différents : sans effet sur la connexion, qui passe par des redirections et un rafraîchissement par jeton, sans cookie tiers ni iframe. Le backend appelle l'introspection par l'URL publique (`KEYCLOAK_URL=https://iabilis.fr`), qui reste valable quel que soit le chemin réseau. Les deux comptes du foyer sont créés à la main dans la console.

Keycloak est en `latest` avec mise à jour automatique : le backend n'utilise que l'introspection standard (RFC 7662), peu exposée aux changements de version.

### 4. Base dans le PostgreSQL du socle

`infra/deploy/create-database.sh` (une fois) : crée la base `lifemanager`, un schéma et un compte par domaine avec des mots de passe aléatoires écrits dans `~/life-manager/.env`, en reprenant le script d'init de dev. Exécuté par `docker exec` dans le conteneur `db` du socle avec son compte administrateur ; aucun autre schéma ni base n'est touché. Les migrations Flyway tournent au démarrage du backend.

PostgreSQL 16 au lieu de 18 en dev : nos migrations n'utilisent rien de propre à la version 18 ; la CI vérifiera les migrations sur une image 16 avant le premier déploiement.

### 5. Images construites par la CI

Workflow sur tag `v*` : build natif du backend (image minimale, utilisateur non root), build de la PWA avec `VITE_OIDC_AUTHORITY=https://iabilis.fr/realms/life-manager`, publication sur `ghcr.io/<propriétaire>/life-manager-api` et `…-web`. Images publiques (dépôt open source, aucun secret dedans) : pas d'identifiant GHCR à stocker sur le VPS.

### 6. Secrets

`infra/deploy/install.sh` crée `~/life-manager` et `~/volumes/life-manager-files`, génère `.env` (droits `600`) : mots de passe des six comptes PostgreSQL, secret du client API, tag déployé. Les secrets du socle ne sont ni lus ni modifiés.

### 7. Sauvegardes

La base `lifemanager` (et le realm, via la base `keycloak`) est déjà incluse dans le `pg_dumpall` nocturne du socle. Seul ajout : `life-manager-files` dans la liste des volumes archivés par `~/infra/backup.sh` (une ligne, validée par Dorian). Base et fichiers sont archivés à quelques secondes d'intervalle : au pire un fichier orphelin. Restauration documentée et testée sur une machine vierge.

**Copie hors du serveur** (choix de Dorian, 2026-11-18) : les archives du socle restent aujourd'hui sur le disque du VPS, non chiffrées ; une panne grave du serveur ou du site de Strasbourg les emporterait avec les données. Chaque nuit, après `backup.sh`, un script `offsite-backup.sh` envoie la dernière archive dans un dépôt **restic** (chiffré côté VPS, mot de passe dans un `.env` en `600`) hébergé sur **OVH Object Storage, région Gravelines** (compatible S3), puis applique la rétention `--keep-daily 7 --keep-weekly 4 --keep-monthly 6 --prune`. restic tourne dans son image officielle (`docker run --rm`), rien n'est installé sur l'hôte. Le conteneur S3 et ses identifiants sont créés par Dorian dans l'espace client OVH ; le mot de passe restic est conservé aussi hors du VPS (gestionnaire de mots de passe), sinon les sauvegardes sont illisibles. Ce script profite à toutes les applications du socle.

### 8. Mise à jour

`infra/deploy/deploy.sh <tag>` : met à jour le tag dans `.env`, `docker compose pull`, `docker compose up -d`, copie le fragment Caddy et recharge Caddy, attend `GET /health = UP`. Retour arrière = relancer avec le tag précédent (migrations Flyway additives ; une migration destructive demandera une sauvegarde préalable explicite).

## Recommandations pour le serveur (hors change, avec accord de Dorian)

1. **SSH** : désactiver l'authentification par mot de passe (`PasswordAuthentication no`), les connexions se faisant par clé.
2. **Disque** : environ 20 Go récupérables en anciennes images et cache de build Docker (`docker builder prune`, `docker image prune`).
3. **Mémoire** : ajouter un fichier d'échange de 2 Go, pour éviter qu'un pic de mémoire fasse tuer un conteneur.
4. **Console Keycloak** : réserver `/admin` au tunnel SSH en le bloquant dans le fragment Caddy de Keycloak.

## Risks / Trade-offs

- **Socle partagé** → une panne de PostgreSQL, Caddy ou Keycloak touche toutes les applications ; en contrepartie une seule instance à maintenir et sauvegarder.
- **Keycloak en `latest`** → une version majeure peut arriver sans relecture ; atténué par le retour arrière automatique du socle et notre usage limité à l'introspection.
- **Mémoire** (1,7 Go libres, pas de swap) → backend natif et PWA estimés à moins de 200 Mo ; à mesurer après le premier déploiement.
- **Disque** (11 Go libres) → pièces jointes limitées à 10 Mo, surveiller le volume `life-manager-files` et les archives locales de sauvegarde, qui le contiennent 14 fois.
- **Build natif en CI (~7 Go de RAM, plusieurs minutes)** → limité aux tags.
- **Sans outbox**, un arrêt brutal pendant une publication d'événement peut désynchroniser une copie locale (membres, échéances) ; risque inchangé.

## Migration Plan

1. Dans la zone DNS de `dorian-grelu.fr`, créer l'enregistrement `maison` vers le VPS (A `152.228.237.190`, AAAA facultatif). 2. Publier un premier tag `v0.1.0` (images). 3. `install.sh`, `create-database.sh`, `create-realm.sh`. 4. Premier `deploy.sh v0.1.0`. 5. Créer les deux comptes dans Keycloak. 6. Ajouter `life-manager-files` à `backup.sh`. 7. Dorian crée le conteneur Object Storage à Gravelines et ses identifiants S3 ; installer `offsite-backup.sh` et sa ligne cron. 8. Vérifier une sauvegarde hors site puis une restauration.

Retour arrière : `docker compose down` dans `~/life-manager`, retirer le fragment Caddy et recharger ; la base et le realm peuvent rester ou être supprimés sans effet sur les autres applications.

## Open Questions

_Aucune : sous-domaine et destination des sauvegardes tranchés par Dorian le 2026-11-18._

## Décisions prises sans Dorian

1. **Se brancher sur le socle plutôt qu'une pile dédiée** — validé par Dorian le 2026-11-18, ainsi que le sous-domaine `maison.dorian-grelu.fr`.
2. **Docker Compose** — déjà la norme du VPS.
3. **PWA servie par un petit conteneur `web`** — alternative : monter `dist/` dans le Caddy du socle. Raison : ne pas modifier le compose du socle, déployer la PWA avec son tag. Revenir en arrière : copier `dist/` dans un volume monté par Caddy et utiliser `file_server`.
4. **Images publiques sur GHCR par la CI sur tag** — alternatives : build sur le VPS, images privées. Raison : le build natif est trop gourmand pour le VPS ; le dépôt est open source. Revenir en arrière : images privées + `docker login` sur le VPS.
5. **Déploiement déclenché manuellement par Dorian (`deploy.sh`)** — alternative : CD par SSH depuis GitHub Actions. Raison : pas de clé du serveur dans la CI. Revenir en arrière : job `workflow_dispatch`.
6. **Introspection par l'URL publique de Keycloak** — alternative : `http://keycloak:8080` sur le réseau `socle`. Raison : l'émetteur des jetons est fixé à `https://iabilis.fr`, l'URL publique évite toute ambiguïté d'hôte. Revenir en arrière : changer `KEYCLOAK_URL`.
