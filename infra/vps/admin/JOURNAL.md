# Journal des interventions

Une entrée par intervention, la plus récente en bas. Heures en UTC.
Modèle : `## AAAA-MM-JJ HH:MM — qui — titre` puis quoi, pourquoi, retour arrière, à vérifier.

## 2026-11-19 13:20 — Claude — audit du serveur

Audit en lecture seule (organisation, sécurité, sauvegardes). Constats : disque à 81 %, SSH acceptant les mots de passe, sauvegardes seulement sur le serveur, journaux Docker sans limite, Keycloak sur `latest`, ni swap ni supervision.

## 2026-11-19 13:25 — Claude — corrections urgentes

- Images Life Manager v0.1.0 à v0.6.3 et cache de build supprimés : disque de 81 % à 22 %.
- SSH par clé uniquement : `/etc/ssh/sshd_config.d/00-cles-uniquement.conf` (lu avant `50-cloud-init.conf`, qui remettait les mots de passe). Retour arrière : supprimer ce fichier, `sudo systemctl reload ssh`.
- `/etc/docker/daemon.json` : journaux limités à 3 × 10 Mo. Actif après un redémarrage de Docker, pour les conteneurs recréés ensuite.
- Conséquence : Dorian ne pouvait plus se connecter depuis WSL (sa clé n'était pas autorisée) ; il a ajouté lui-même sa clé.

## 2026-11-19 13:45 — Claude + Dorian — copie des sauvegardes vers OneDrive

rclone installé ; `~/infra/onedrive/send-backup.sh` chaque nuit à 3 h 30 (cron), copie chiffrée dans `Sauvegardes/VPS`, 30 jours gardés. Autorisation Microsoft et phrase de chiffrement saisies par Dorian. Piège : rclone 1.60 choisissait un lecteur caché, corrigé (`drive_id` du « OneDrive (personal) »). 14 archives envoyées, relecture déchiffrée vérifiée.
**À vérifier le 2026-11-20** : `tail ~/infra/onedrive/onedrive.log` (premier envoi automatique).

## 2026-11-19 14:00 — Claude — Keycloak fixé sur la branche 26.8

`~/keycloak/docker-compose.yml` : `keycloak:latest` → `keycloak:26.8` ; en-tête de `auto-update.sh` mis à jour. Copies : `*.avant-version-fixe`. Aucun redémarrage ; l'image 26.8 (même 26.8.2, construction plus récente) sera appliquée par `auto-update.sh` la nuit suivante.
**À vérifier le 2026-11-20** : `tail -20 ~/keycloak/auto-update.log` (« mise a jour reussie »).
**En suspens** : l'ADR-0032 du dépôt mybook décrit encore le suivi de `latest`.

## 2026-11-19 14:10 — Claude — documentation d'administration

Installation de `~/AGENTS.md`, `~/CLAUDE.md` et `~/admin/` (inventaire, procédures, journal, `etat.sh`).

## 2026-11-19 13:55 — Claude — utilisateur dédié à l'IA

Choix de Dorian : « séparé mais complet ». Utilisateur `ia` : `sudo` sans mot de passe (`/etc/sudoers.d/90-ia`), groupes `docker` et `ubuntu`, pas de mot de passe, clé `claude-code-life-manager`, `/home/ia/CLAUDE.md` qui renvoie vers `~ubuntu/AGENTS.md`. La clé `claude-code-life-manager` est **retirée d'`ubuntu`** (copie : `~/.ssh/authorized_keys.avant-ia`) ; refus vérifié. Les scripts de l'IA se connectent en `ia` et travaillent en `ubuntu` par `sudo -iu ubuntu`.
Retour arrière : `sudo userdel -r ia ; sudo rm /etc/sudoers.d/90-ia` et remettre la clé dans `~ubuntu/.ssh/authorized_keys`.

## Points ouverts (à décider avec Dorian)

- Swap de 2 Go.
- Supervision et alerte (un site qui tombe ne prévient personne).
- La session Claude de mybook utilise encore `ubuntu` (clé `claude-deploy-mybook`, en double) : la basculer aussi sur `ia` ?
- Versionner `~/infra`, `~/keycloak`, `~/portainer` dans un dépôt privé.
- Ménage du dossier personnel : `%{redirect_url}n`, `get-docker.sh`, `test-newsletter-brevo.sh`, `conteneurs-avant-reboot.txt`, `~/mybook/backups` (arrêté en août), `~/backups` (vide), clé SSH `claude-deploy-mybook` en double.
- Le rôle PostgreSQL `site` (mybook) est superutilisateur.
