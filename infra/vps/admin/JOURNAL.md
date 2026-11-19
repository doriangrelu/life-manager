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

## 2026-11-19 14:05 — Claude — clé de mybook basculée sur « ia »

Clé `claude-deploy-mybook` (présente deux fois, même empreinte ; aucune connexion depuis au moins 4 semaines ; pas utilisée par la CI) retirée d'`ubuntu` et ajoutée à `ia`. Copie : `~/.ssh/authorized_keys.avant-mybook-ia`. `ubuntu` n'a plus que les clés de Dorian.
**À faire** : les sessions Claude de mybook doivent se connecter en `ia@` et travailler avec `sudo -iu ubuntu` (non testé : la clé privée est sur le PC de Dorian).

## 2026-11-19 14:10 — Claude — swap de 2 Go

`/swapfile` (2 Go, droits 600) activé et ajouté à `/etc/fstab` ; `vm.swappiness=10` (`/etc/sysctl.d/90-swap.conf`) : utilisé seulement en dernier recours. Retour arrière : `sudo swapoff /swapfile`, retirer la ligne de `/etc/fstab`, supprimer le fichier.

## 2026-11-19 14:05 — Claude — interface web Docker préparée (Dozzle)

Choix de Dorian : Dozzle (léger) plutôt que Portainer. `~/dozzle` : Dozzle v11.4.3 + oauth2-proxy v7.15.6 (realm Keycloak `infra`, client `oauth2-proxy`), Dozzle sur un réseau privé joignable seulement par oauth2-proxy. Dozzle démarré seul pour test (9 Mo, santé OK, injoignable de l'extérieur). Secret de cookie régénéré ; `~/dozzle` ajouté à `~/infra/backup.sh` (copie : `backup.sh.avant-dozzle`). La liste d'adresses reprise de `~/portainer` était vide.
**En attente de Dorian** : DNS `docker.dorian-grelu.fr`, realm `infra` + client + compte avec OTP, puis `~/dozzle/configurer-acces.sh`. Ensuite : démarrage d'oauth2-proxy et route Caddy (README `infra/vps/dozzle`).
Retour arrière : `cd ~/dozzle && docker compose down`, retirer `dozzle` de la ligne `infra keycloak dozzle` de `backup.sh`.

## 2026-11-19 14:30 — Claude + Dorian — interface web Docker en service

DNS `docker.dorian-grelu.fr` créé par Dorian. Realm `infra` importé par Dorian depuis un JSON généré par Claude (client `oauth2-proxy` avec secret aléatoire jamais affiché, aussi écrit dans `~/dozzle/.env` ; mapper d'audience ; utilisateur `dorian` avec OTP requis, mot de passe défini par Dorian) ; Dorian a ensuite durci le realm et activé *Always use lightweight access token*. oauth2-proxy démarré (proxys de confiance limités au réseau `socle`, 172.19.0.0/16), route `~/infra/conf.d/docker.caddy` ajoutée, Caddy rechargé sans coupure, certificat obtenu. Vérifié : redirection vers Keycloak `infra`, autres sites OK.
**À vérifier** : première connexion complète de Dorian (OTP, puis Dozzle affiche son adresse).
Couper l'interface : `cd ~/dozzle && docker compose stop`, supprimer `~/infra/conf.d/docker.caddy`, recharger Caddy.

## 2026-11-19 14:30 — Claude — erreur 500 à la connexion corrigée

Au retour de Keycloak, oauth2-proxy cherchait le champ `groups` absent du jeton et appelait `userinfo`, refusé en 401 (jeton d'accès allégé du realm). Ajout de `OAUTH2_PROXY_SKIP_CLAIMS_FROM_PROFILE_URL=true` (les groupes ne servent pas). oauth2-proxy recréé. **Connexion complète validée par Dorian** (Keycloak + OTP → Dozzle).

## 2026-11-19 14:36 — Claude + Dorian — alertes Healthchecks.io

Compte et surveillance « VPS » créés par Dorian (période 10 min, grâce 20 min) ; adresse de signal enregistrée par lui (`~/admin/.healthchecks-url`, 600). `vps-signal-vie.timer` (toutes les 10 min, en `ubuntu`) lance `~/admin/signal-vie.sh` : `etat.sh`, 2e essai 2 min plus tard en cas d'écart, puis signal OK ou échec avec le détail. Premier tour réussi ; échec de test volontaire envoyé, **alerte reçue par Dorian**.
Retour arrière : `sudo systemctl disable --now vps-signal-vie.timer`.

## Points ouverts (à décider avec Dorian)

- Supprimer `~/portainer` (préparation remplacée par `~/dozzle`).
- Versionner `~/infra`, `~/keycloak`, `~/portainer` dans un dépôt privé.
- Ménage du dossier personnel : `%{redirect_url}n`, `get-docker.sh`, `test-newsletter-brevo.sh`, `conteneurs-avant-reboot.txt`, `~/mybook/backups` (arrêté en août), `~/backups` (vide), clé SSH `claude-deploy-mybook` en double.
- Le rôle PostgreSQL `site` (mybook) est superutilisateur.
