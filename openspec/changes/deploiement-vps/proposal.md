## Why

Le MVP (budget, véhicules, courses, rappels) est fonctionnel en local mais n'est utilisable par le foyer qu'une fois hébergé. La vision (`docs/vision.md`) impose un auto-hébergement sur un VPS OVH, un backend natif GraalVM pour limiter les ressources, et des données sensibles à protéger et sauvegarder dès le départ.

Le VPS de Dorian héberge déjà d'autres applications sur un **socle partagé** : Caddy (HTTPS, routage par fragments dans `~/infra/conf.d/`), PostgreSQL 16 et Keycloak (`iabilis.fr`), reliés par le réseau Docker `socle`, avec une sauvegarde nocturne de toutes les bases. Life Manager se branche sur ce socle au lieu d'installer sa propre pile.

## What Changes

- **Images** : image du backend compilé en natif et image de la PWA (fichiers statiques), construites par GitHub Actions et publiées sur GitHub Container Registry à chaque tag de version.
- **Pile Life Manager** : un `docker-compose.prod.yml` (backend natif + PWA) dans `~/life-manager` sur le VPS, raccordé au réseau `socle`. Aucun port publié : seul le Caddy du socle reçoit le trafic.
- **Routage** : un fragment `life-manager.caddy` déposé dans `~/infra/conf.d/` (sous-domaine de l'application, PWA, relais `/api`, en-têtes de sécurité).
- **Authentification** : un realm `life-manager` créé dans le Keycloak existant (inscription fermée, URL de redirection du sous-domaine).
- **Base** : une base `lifemanager` et un compte par domaine métier, créés une fois dans le PostgreSQL du socle.
- **Secrets** : générés à l'installation dans `~/life-manager/.env` (droits `600`), jamais dans le dépôt ni les images.
- **Sauvegardes** : la base est déjà couverte par le `pg_dumpall` du socle ; le dossier des pièces jointes est ajouté à la sauvegarde du socle. La copie hors du serveur est assurée par la sauvegarde automatique du VPS chez OVH. Restauration documentée et testée.
- **Mise à jour** : script `deploy.sh <tag>` lancé par Dorian (tirer les images, redémarrer, attendre le contrôle de santé), retour arrière sur le tag précédent.
- **Documentation** : `docs/deploiement.md` (installation, mise à jour, sauvegarde, restauration).

## Capabilities

### New Capabilities

- `deploiement` : hébergement de production sur le socle partagé du VPS (HTTPS, secrets hors du dépôt, sauvegardes, mise à jour et retour arrière).

### Modified Capabilities

_Aucune._

## Impact

- Nouveaux fichiers : `infra/deploy/docker-compose.prod.yml`, `infra/caddy/life-manager.caddy`, `infra/keycloak/realm-life-manager.prod.json`, `infra/deploy/` (scripts), Dockerfiles backend et PWA, workflow GitHub Actions de publication.
- PWA : URL Keycloak de production injectée au build (`VITE_OIDC_AUTHORITY`).
- Socle du VPS : un fragment Caddy, un realm, une base et une ligne dans `~/infra/backup.sh`. Les autres applications ne sont ni arrêtées ni modifiées.
- Aucun changement de code métier. Un correctif technique : en natif, chaque domaine n'applique que ses propres migrations Flyway.

## Hors périmètre

- Haute disponibilité, plusieurs serveurs, Kubernetes.
- Déploiement continu automatique à chaque commit (le déploiement reste déclenché par Dorian).
- Supervision avancée (métriques, alertes) au-delà du contrôle de santé et des journaux Docker.
- Changements du socle qui ne concernent pas Life Manager (durcissement SSH, swap, nettoyage Docker, console Keycloak) : listés comme recommandations dans le design, appliqués seulement avec l'accord de Dorian.
- Web Push, envoi de courriels.
