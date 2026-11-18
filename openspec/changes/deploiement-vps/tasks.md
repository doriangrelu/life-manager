> Proposition à relire par Dorian (voir « Open Questions » du design) avant toute implémentation. Rien n'est fait sur le serveur sans son accord.

## 1. Images

- [ ] 1.1 [infra/backend] Dockerfile de l'image native du backend (utilisateur non root, image minimale) ; vérifié par un `docker run` local qui répond `UP` sur `/health` et accepte l'envoi d'un PDF en pièce jointe (détection de type sur l'exécutable natif)
- [ ] 1.2 [infra/frontend] Image de la PWA (fichiers statiques) avec `VITE_OIDC_AUTHORITY` passé au build ; vérifiée localement
- [ ] 1.3 [ci] Migrations Flyway vérifiées aussi sur PostgreSQL 16 (version du socle)
- [ ] 1.4 [ci] Workflow sur tag `v*` : build et publication des deux images sur GHCR ; vérifié avec un tag de test

## 2. Pile Life Manager

- [ ] 2.1 [infra] `docker-compose.prod.yml` (api, web) sur le réseau externe `socle`, sans port publié, limites de ressources, volume des pièces jointes, contrôles de santé ; vérifié localement avec un réseau `socle` de test
- [ ] 2.2 [infra/caddy] Fragment `life-manager.caddy` : PWA, relais `/api`, en-têtes de sécurité (CSP avec `iabilis.fr` et `blob:`)
- [ ] 2.3 [infra/keycloak] Realm de production (URL du sous-domaine, inscription fermée, secret client lu dans l'environnement)

## 3. Installation et mise à jour

- [ ] 3.1 [infra/deploy] `install.sh` : `~/life-manager`, dossier des pièces jointes, `.env` en `600` avec secrets aléatoires
- [ ] 3.2 [infra/deploy] `create-database.sh` : base `lifemanager`, un schéma et un compte par domaine, sans toucher aux autres bases
- [ ] 3.3 [infra/deploy] `create-realm.sh` : import du realm par `kcadm.sh` dans le Keycloak existant
- [ ] 3.4 [infra/deploy] `deploy.sh <tag>` : pull, redémarrage, copie du fragment et rechargement de Caddy, attente du contrôle de santé ; retour arrière vérifié

## 4. Sauvegardes

- [ ] 4.1 [infra/backup] Ajout du dossier des pièces jointes à `~/infra/backup.sh` (avec accord de Dorian) ; archive vérifiée
- [ ] 4.2 [doc] Procédure de restauration de Life Manager, testée sur une machine vierge

## 5. Documentation

- [ ] 5.1 [doc] `docs/deploiement.md` : DNS, prérequis du socle, installation, mise à jour, sauvegarde, restauration, recommandations serveur ; README à jour
