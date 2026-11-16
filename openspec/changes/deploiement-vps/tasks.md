> Proposition seulement : à relire par Dorian (voir « Open Questions » du design) avant toute implémentation.

## 1. Images

- [ ] 1.1 [infra/backend] Dockerfile de l'image native du backend (utilisateur non root, image minimale) ; vérifié par un `docker run` local qui répond `UP` sur `/health`
- [ ] 1.2 [infra/frontend] Image de la PWA (fichiers statiques) avec `VITE_OIDC_AUTHORITY` passé au build ; vérifiée localement
- [ ] 1.3 [ci] Workflow sur tag `v*` : build et publication des deux images sur GHCR ; vérifié avec un tag de test

## 2. Pile de production

- [ ] 2.1 [infra] `docker-compose.prod.yml` (caddy, api, postgres, keycloak en mode production, backup), réseau interne, contrôles de santé ; vérifié sur une machine de test
- [ ] 2.2 [infra/caddy] `Caddyfile` : PWA, relais `/api`, sous-domaine `auth`, en-têtes de sécurité, redirection HTTP → HTTPS
- [ ] 2.3 [infra/keycloak] Realm de production (URL du domaine, inscription fermée, secret client lu dans l'environnement)

## 3. Installation et mise à jour

- [ ] 3.1 [infra/deploy] `install.sh` : génération des secrets (`.env` en `600`), arborescence `/opt/life-manager`
- [ ] 3.2 [infra/deploy] `deploy.sh <tag>` : pull, redémarrage, attente du contrôle de santé ; retour arrière vérifié

## 4. Sauvegardes

- [ ] 4.1 [infra/backup] Sauvegarde quotidienne restic (pg_dump des deux bases) avec rétention 7/4/6
- [ ] 4.2 [doc] Procédure de restauration, testée sur une machine vierge

## 5. Documentation

- [ ] 5.1 [doc] `docs/deploiement.md` : DNS, durcissement du VPS, installation, mise à jour, sauvegarde, restauration ; README à jour
