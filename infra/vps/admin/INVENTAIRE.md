# Inventaire du VPS

État relevé le 2026-11-19. À corriger dès qu'une ligne devient fausse.

## Machine

| Élément | Valeur |
|---|---|
| Hébergeur | OVH, VPS 2 cœurs, 3,7 Go de mémoire, swap de 2 Go (`/swapfile`, `vm.swappiness=10` dans `/etc/sysctl.d/90-swap.conf`), disque 38 Go |
| Système | Ubuntu 26.04 LTS, horloge UTC |
| Accès | SSH sur 152.228.237.190, **par clé uniquement** ; secours : console KVM ou mode rescue de l'espace client OVH |
| Utilisateur `ubuntu` | propriétaire de tous les fichiers ; `sudo` sans mot de passe, groupe `docker`. Clés : Dorian seulement (PC Windows et WSL) |
| Utilisateur `ia` | réservé à l'IA (créé le 2026-11-19) : `sudo` sans mot de passe (`/etc/sudoers.d/90-ia`), groupes `docker` et `ubuntu`, pas de mot de passe. Clés : `claude-code-life-manager` (sessions Claude de Life Manager), `claude-deploy-mybook` (sessions Claude de mybook). Couper l'accès de l'IA : `sudo usermod -L -e 1 ia` ou vider `/home/ia/.ssh/authorized_keys` |
| Pare-feu | `ufw` : entrée refusée sauf 22, 80, 443 |
| fail2ban | une prison `sshd` |
| Réglages ajoutés | `/etc/ssh/sshd_config.d/00-cles-uniquement.conf` ; `/etc/docker/daemon.json` (journaux 3 × 10 Mo, pour les conteneurs créés après un redémarrage de Docker) |

## Sites et chemin d'une requête

Internet → Caddy (`infra-caddy-1`, ports 80/443, certificats automatiques) → conteneur cible, par le réseau Docker `socle` (tous les conteneurs y sont).

| Domaine | Fichier Caddy | Conteneur cible | Projet |
|---|---|---|---|
| maison.dorian-grelu.fr | `~/infra/conf.d/life-manager.caddy` | `life-manager-api` (API), `life-manager-web` (application web) | Life Manager |
| thierrylacassin-auteur.fr (+ www), leblogdethierry.fr (+ www) | `~/infra/conf.d/mybook.caddy` | `mybook-app-1` | mybook |
| iabilis.fr | `~/infra/conf.d/keycloak.caddy` | `keycloak-keycloak-1` | Keycloak (connexion de tous les sites) |

`~/infra/Caddyfile` importe `conf.d/*.caddy`.

## Dossiers de `/home/ubuntu`

| Dossier | Contenu | Versionné ? | Démarrage |
|---|---|---|---|
| `~/infra` | socle : Caddy + PostgreSQL 16 (`docker-compose.yml`), `backup.sh`, `restore.sh`, `deploy.sh`, `backups/`, `onedrive/` | **non** (seulement dans les sauvegardes) | `docker compose up -d` dans le dossier |
| `~/keycloak` | Keycloak, `auto-update.sh`, `themes/` | **non** | idem |
| `~/life-manager` | dépôt git placé sur le tag déployé ; secrets dans `.env.prod` | git (github `doriangrelu/life-manager`) | `infra/deploy/deploy.sh <tag>` |
| `~/mybook` | dépôt git du site de l'auteur ; secrets dans `.env` | git (github `doriangrelu/lacassinauteur`) | voir `~/mybook/docs/mode-operatoire-deploiement.md` |
| `~/portainer` | préparé, **jamais démarré** (voir son README) | non | — |
| `~/volumes` | données montées dans les conteneurs : `db-data`, `images-data`, `logs-data`, `caddy-data`, `caddy-config`, `life-manager-files` | — | — |
| `~/admin` | cette documentation et `etat.sh` | source dans `life-manager/infra/vps/admin` | — |

## Conteneurs

| Conteneur | Image | Compose |
|---|---|---|
| `infra-caddy-1` | `caddy:2` | `~/infra/docker-compose.yml` |
| `infra-db-1` | `postgres:16`, port 5432 sur 127.0.0.1 seulement | `~/infra/docker-compose.yml` |
| `keycloak-keycloak-1` | `quay.io/keycloak/keycloak:26.8` (branche fixée) | `~/keycloak/docker-compose.yml` |
| `life-manager-api` | `ghcr.io/doriangrelu/life-manager-api:<tag>` (exécutable natif) | `~/life-manager/infra/deploy/docker-compose.prod.yml` |
| `life-manager-web` | `ghcr.io/doriangrelu/life-manager-web:<tag>` | idem |
| `mybook-app-1` | `mybook-app` (construite sur place) | `~/mybook/docker-compose.prod.yml` |

## Bases PostgreSQL (`infra-db-1`)

| Base | Utilisée par |
|---|---|
| `site` | mybook (rôle `site`, superutilisateur : c'est aussi l'administrateur de PostgreSQL) |
| `keycloak` | Keycloak (rôle `keycloak`) |
| `lifemanager` | Life Manager : un schéma et un rôle par domaine (`identity_app`, `household_app`, `budget_app`, `vehicle_app`, `shopping_app`, `reminder_app`, `bills_app`) |

Le nom de l'administrateur est la variable `POSTGRES_USER` de `~/infra/.env` : ne lire **que** ce nom.

## Secrets (emplacement seulement, ne jamais lire les valeurs)

`~/infra/.env`, `~/keycloak/.env`, `~/life-manager/.env.prod`, `~/mybook/.env`, `~/portainer/.env` (tous en droits 600) ; `~/.config/rclone/rclone.conf` (jeton OneDrive + phrase de chiffrement masquée). La phrase de chiffrement OneDrive est aussi dans le gestionnaire de mots de passe de Dorian.

## Tâches planifiées (UTC)

| Heure | Quoi | Où | Journal |
|---|---|---|---|
| 3 h 00 | sauvegarde complète (bases + volumes + config `~/infra` et `~/keycloak`), 14 gardées | cron `ubuntu` → `~/infra/backup.sh` | `~/infra/backup.log` |
| 3 h 30 | copie chiffrée vers OneDrive (`Sauvegardes/VPS`), 30 jours gardés | cron `ubuntu` → `~/infra/onedrive/send-backup.sh` | `~/infra/onedrive/onedrive.log` |
| 4 h 15 | correctifs Keycloak de la branche fixée, test public + retour arrière automatique | cron `ubuntu` → `~/keycloak/auto-update.sh` | `~/keycloak/auto-update.log` |
| 5 h 00 | mises à jour système, redémarrage seulement si nécessaire, puis vérification | `vps-maintenance.timer` | `/var/log/vps-maintenance.log` |
| démarrage | vérification : conteneurs relancés, sites qui répondent | `vps-healthcheck.service` | idem |

Sources de la maintenance : `life-manager/infra/vps/` (installation : `infra/vps/install-maintenance.sh`).

## Ce qui manque (connu)

Pas de supervision ni d'alerte (personne n'est prévenu si un site tombe) ; `~/infra` et `~/keycloak` non versionnés ; l'utilisateur `ia` a tous les droits (choix de Dorian : séparé mais complet, les limites sont des consignes).
