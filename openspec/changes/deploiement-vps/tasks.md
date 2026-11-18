> Partie dépôt faite et vérifiée en local (socle simulé). Les tâches restantes se font sur le serveur ou sur GitHub, avec l'accord de Dorian.

## 1. Images

- [x] 1.1 [infra/backend] Dockerfile de l'image native du backend (utilisateur non root, image minimale) ; vérifié par un `docker run` local qui répond `UP` sur `/health` et accepte l'envoi d'un PDF en pièce jointe (détection de type sur l'exécutable natif)
- [x] 1.2 [infra/frontend] Image de la PWA (fichiers statiques) avec `VITE_OIDC_AUTHORITY` passé au build ; vérifiée localement
- [x] 1.3 [ci] Migrations Flyway vérifiées aussi sur PostgreSQL 16 (version du socle)
- [x] 1.4 [ci] Workflow sur tag `v*` : build et publication des deux images sur GHCR ; vérifié avec `v0.1.0` (images publiques, tirées sans identifiant)

## 2. Pile Life Manager

- [x] 2.1 [infra] `docker-compose.prod.yml` (api, web) sur le réseau externe `socle`, sans port publié, limites de ressources, volume des pièces jointes ; vérifié localement avec un réseau `socle` de test
- [x] 2.2 [infra/caddy] Fragment `life-manager.caddy` : PWA, relais `/api`, en-têtes de sécurité (CSP avec `iabilis.fr` et `blob:`)
- [x] 2.3 [infra/keycloak] Realm de production (redirections vers `https://maison.dorian-grelu.fr`, inscription fermée, secret client injecté à l'import)

## 3. Installation et mise à jour

- [x] 3.1 [infra/deploy] `install.sh` : `~/life-manager`, dossier des pièces jointes, `.env.prod` en `600` avec secrets aléatoires (ou secret du realm importé à la main)
- [x] 3.2 [infra/deploy] `create-database.sh` : base `lifemanager`, un schéma et un compte par domaine, sans toucher aux autres bases
- [x] 3.3 [infra/deploy] `create-realm.sh` : import du realm par `kcadm.sh` dans le Keycloak existant (variante de l'import manuel)
- [ ] 3.4 [infra/deploy] `deploy.sh <tag>` : pull, redémarrage, attente du contrôle de santé, copie du fragment et rechargement de Caddy — fait et vérifié en local ; retour arrière entre deux vrais tags à vérifier sur le VPS

## 4. Sauvegardes

- [ ] 4.1 [infra/backup] Ajout du dossier des pièces jointes à `~/infra/backup.sh` (avec accord de Dorian) ; archive vérifiée
- [ ] 4.2 [doc] Procédure de restauration de Life Manager depuis une archive du socle, testée sur une machine vierge

## 5. Documentation

- [x] 5.1 [doc] `docs/deploiement.md` : DNS, prérequis du socle, installation, mise à jour, sauvegarde, restauration, recommandations serveur ; README à jour
