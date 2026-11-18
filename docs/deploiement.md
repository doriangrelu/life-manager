# Déploiement sur le VPS

Life Manager tourne sur le VPS OVH, branché sur le **socle partagé** déjà en place pour les autres applications :

| Élément | Fourni par | Rôle pour Life Manager |
|---|---|---|
| Caddy (`~/infra`) | socle | HTTPS de `maison.dorian-grelu.fr`, routage via `~/infra/conf.d/life-manager.caddy` |
| PostgreSQL 16 (`~/infra`, conteneur `db`) | socle | base `lifemanager`, un compte et un schéma par domaine |
| Keycloak (`~/keycloak`, `https://iabilis.fr`) | socle | realm `life-manager` |
| Sauvegarde nocturne (`~/infra/backup.sh`) | socle | `pg_dumpall` + dossiers de `~/volumes` |
| `life-manager-api`, `life-manager-web` | ce dépôt (`~/life-manager`) | backend natif et PWA, sur le réseau Docker `socle`, sans port publié |

Images publiées par la CI à chaque tag `v*` : `ghcr.io/doriangrelu/life-manager-api` et `ghcr.io/doriangrelu/life-manager-web`.

À chaque commit sur `main`, la CI construit aussi l'image native et rejoue un parcours d'usage complet sur l'API compilée ([`infra/ci/native-smoke.sh`](../infra/ci/native-smoke.sh)) : les erreurs propres à l'exécutable natif sont arrêtées avant toute version. Avant de publier un tag, vérifier que le job « Exécutable natif » de `main` est vert.

## Première installation

Prérequis : socle en service, réseau Docker `socle`, enregistrement DNS `maison.dorian-grelu.fr` (A `152.228.237.190`, AAAA `2001:41d0:404:200::dbb` facultatif).

1. **Publier une version** : `git tag v0.1.0 && git push origin v0.1.0` (workflow « Publication des images »).
   La première fois seulement, rendre les deux paquets publics sur GitHub (profil → Packages → paquet → Package settings → Change visibility) : le VPS les tire sans identifiant.
2. **Realm** : dans la console Keycloak (`https://iabilis.fr/admin`), *Create realm* → importer le fichier JSON fourni (secret client déjà rempli, à ne jamais commiter). Il reprend [`infra/keycloak/realm-life-manager.prod.json`](../infra/keycloak/realm-life-manager.prod.json). Variante en ligne de commande : `infra/deploy/create-realm.sh`.
3. **Sur le VPS** :
   ```bash
   git clone https://github.com/doriangrelu/life-manager.git ~/life-manager
   cd ~/life-manager
   KEYCLOAK_API_CLIENT_SECRET=<secret du client life-manager-api> infra/deploy/install.sh
   infra/deploy/create-database.sh
   infra/deploy/deploy.sh v0.1.0
   ```
   - `install.sh` : dossier des pièces jointes `~/volumes/life-manager-files`, secrets dans `~/life-manager/.env.prod` (droits 600).
   - `create-database.sh` : base `lifemanager` et ses sept comptes (un par domaine) dans le PostgreSQL du socle. Les autres bases ne sont pas touchées.
4. **Comptes du foyer** : console Keycloak → realm `life-manager` → *Users* → créer chaque membre et lui donner un mot de passe (12 caractères minimum). L'inscription libre est fermée.
5. **Sauvegarde** : ajouter `life-manager-files` à la liste des dossiers de `~/infra/backup.sh` (ligne `sudo tar -cf … images-data logs-data caddy-data caddy-config`), puis vérifier l'archive suivante. Fait le 2026-11-18 sur le VPS actuel (copie de l'original : `~/infra/backup.sh.avant-life-manager`).

Les scripts refusent de s'exécuter une deuxième fois (secrets, base ou realm déjà présents) : rien n'est écrasé.

## Mettre à jour

Pas à pas détaillé : [modop-deploiement.md](modop-deploiement.md).

```bash
cd ~/life-manager && infra/deploy/deploy.sh v0.2.0
```

Le script place le dépôt sur le tag, tire les images, redémarre l'API et la PWA, attend que l'API réponde `UP`, puis installe le routage et recharge Caddy (sans coupure pour les autres sites). Les migrations de base s'appliquent au démarrage de l'API.

**Version qui apporte un nouveau domaine** (ex. v0.3.0 et `bills`) : créer d'abord son compte et son schéma, puis déployer. Le script génère le mot de passe manquant dans `.env.prod` et ne touche à rien d'autre ; il peut être relancé sans risque.

```bash
cd ~/life-manager && git fetch --tags && git checkout v0.3.0
infra/deploy/add-domain-schema.sh bills
infra/deploy/deploy.sh v0.3.0
```

**Retour arrière** : relancer avec le tag précédent, affiché à la fin de chaque déploiement. Les migrations sont additives ; avant une version qui supprimerait des données, faire une sauvegarde à la main (`~/infra/backup.sh`).

## Page de connexion (thème Keycloak)

Le thème `life-manager` vit dans [`infra/keycloak/themes/life-manager`](../infra/keycloak/themes/life-manager) et, sur le VPS, dans `~/keycloak/themes/life-manager`. Pour le mettre à jour :

```bash
# Depuis le dépôt, sur le PC :
tar -C infra/keycloak/themes -cf - life-manager | ssh ubuntu@152.228.237.190 'rm -rf ~/keycloak/themes/life-manager && tar -C ~/keycloak/themes -xf -'
# Sur le VPS (≈ 1 minute de coupure de la connexion pour toutes les applications du serveur) :
docker compose -f ~/keycloak/docker-compose.yml restart
```

Le thème est choisi dans la console : realm `life-manager` → *Realm settings* → *Themes* → *Login theme*.

## Sauvegarde et restauration

Chaque nuit à 3 h, `~/infra/backup.sh` archive toutes les bases (`pg_dumpall`, comptes compris) et les dossiers de `~/volumes`. Les archives restent 14 jours dans `~/infra/backups`, et la sauvegarde automatique du VPS chez OVH en garde une copie hors du serveur.

Restaurer Life Manager sur une machine neuve :

1. Restaurer le socle (`~/infra/restore.sh` avec l'archive) : bases, comptes PostgreSQL et realm reviennent ensemble.
2. Recopier `life-manager-files` depuis l'archive dans `~/volumes/life-manager-files` (propriétaire `65532:65532`).
3. Recopier `~/life-manager/.env.prod` (gardé hors du dépôt), puis `infra/deploy/deploy.sh <dernier tag>`.

## Dépannage

| Symptôme | Piste |
|---|---|
| `deploy.sh` : « le service ne répond pas » | `docker logs life-manager-api` (connexion base, secret Keycloak) |
| Connexion refusée après Keycloak | secret du client `life-manager-api` différent de `.env.prod` ; URL de redirection du realm |
| Certificat absent | DNS de `maison.dorian-grelu.fr` pas encore propagé : `docker logs infra-caddy-1` |
| Pièce jointe refusée en 500 | droits du dossier `~/volumes/life-manager-files` (doit appartenir à `65532`) |

## Recommandations pour le serveur

Hors du périmètre de Life Manager, à appliquer seulement si Dorian le décide :

- désactiver la connexion SSH par mot de passe (`PasswordAuthentication no`) ;
- libérer l'espace des anciennes images Docker (`docker builder prune`, `docker image prune`) ;
- ajouter un fichier d'échange de 2 Go ;
- bloquer `/admin` de Keycloak depuis Internet (accès par tunnel SSH).
