## Why

Le MVP (budget, véhicules, courses, rappels) est fonctionnel en local mais n'est utilisable par le foyer qu'une fois hébergé. La vision (`docs/vision.md`) impose un auto-hébergement sur un VPS OVH, un backend natif GraalVM pour limiter les ressources, et des données sensibles à protéger et sauvegarder dès le départ.

## What Changes

- **Images** : image du backend compilé en natif et image de la PWA (fichiers statiques), construites par GitHub Actions et publiées sur GitHub Container Registry à chaque tag de version.
- **Pile de production** : un `docker-compose.prod.yml` sur le VPS avec Caddy (HTTPS automatique Let's Encrypt, reverse proxy), le backend natif, PostgreSQL et Keycloak. Seuls les ports 80 et 443 sont exposés.
- **Configuration** : tous les secrets (mots de passe PostgreSQL par domaine, secret du client `life-manager-api`, admin Keycloak) dans un fichier `.env` présent uniquement sur le serveur, généré par un script d'installation ; realm Keycloak de production (URL de redirection du domaine, inscription fermée).
- **Sauvegardes** : sauvegarde quotidienne chiffrée de PostgreSQL (bases applicative et Keycloak) avec restic vers un stockage externe, rétention 7 jours / 4 semaines / 6 mois, et procédure de restauration testée.
- **Mise à jour** : script `deploy.sh` lancé par Dorian sur le VPS (tirer les images d'un tag, redémarrer), avec retour arrière sur le tag précédent.
- **Durcissement minimal du serveur** documenté : SSH par clé uniquement, pare-feu, mises à jour de sécurité automatiques.
- **Documentation** : `docs/deploiement.md` (installation, mise à jour, sauvegarde, restauration).

## Capabilities

### New Capabilities

- `deploiement` : hébergement de production sur un VPS (HTTPS, secrets hors du dépôt, sauvegardes, mise à jour et retour arrière).

### Modified Capabilities

_Aucune._

## Impact

- Nouveaux fichiers : `docker-compose.prod.yml`, `infra/caddy/Caddyfile`, `infra/keycloak/realm-life-manager.prod.json`, `infra/deploy/` (scripts), Dockerfiles backend et PWA, workflow GitHub Actions de publication.
- PWA : URL Keycloak de production injectée au build (`VITE_OIDC_AUTHORITY`).
- Aucun changement de code métier.

## Hors périmètre

- Haute disponibilité, plusieurs serveurs, Kubernetes.
- Déploiement continu automatique à chaque commit (le déploiement reste déclenché par Dorian).
- Supervision avancée (métriques, alertes) au-delà du contrôle de santé et des journaux Docker.
- Web Push, envoi de courriels.
