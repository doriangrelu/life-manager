# Procédures

Chaque fiche dit : quand l'utiliser, le niveau d'autonomie (voir `~/AGENTS.md`), les commandes, la vérification.
Toujours commencer par `~/admin/etat.sh` et finir par une entrée dans `~/admin/JOURNAL.md`.

## 1. Vérifier l'état du serveur — libre

```bash
~/admin/etat.sh                      # résumé, code de sortie 1 s'il y a un écart
sudo systemctl start vps-healthcheck # vérification complète (relance un conteneur manquant), résultat dans /var/log/vps-maintenance.log
```

## 2. Un site ne répond plus — libre pour le diagnostic

1. `docker ps -a` : le conteneur tourne-t-il ? redémarre-t-il en boucle (`Restarting`) ?
2. `docker logs --tail 100 <conteneur>` ; pour Caddy : `docker logs --tail 50 infra-caddy-1` (certificat, routage).
3. Si un conteneur **applicatif** est arrêté : `docker start <conteneur>` (annoncer). Pour Caddy, PostgreSQL ou Keycloak : **demander**.
4. Si le conteneur redémarre en boucle : ne pas insister, lire les journaux, rapporter la cause probable à Dorian.
5. Vérifier : `etat.sh`.

## 3. Disque presque plein (plus de 80 %) — annoncer

```bash
df -h / ; docker system df
docker images --format '{{.Repository}}:{{.Tag}} {{.CreatedSince}} {{.Size}}' | sort
docker image rm ghcr.io/doriangrelu/life-manager-api:<vieux tag> ghcr.io/doriangrelu/life-manager-web:<vieux tag>   # garder les 2 dernières versions
docker builder prune -af     # cache de build (mybook se reconstruit plus lentement une fois)
docker image prune -f        # images orphelines
```
Ne jamais toucher aux volumes ni à `~/infra/backups`. Journaux du système : `sudo journalctl --vacuum-size=200M`.

## 4. Déployer une version de Life Manager — demander

Procédure complète : `~/life-manager/docs/deploiement.md` et `docs/modop-deploiement.md`.
```bash
cd ~/life-manager && infra/deploy/deploy.sh vX.Y.Z
```
Le script attend que l'API réponde `UP` et recharge Caddy sans couper les autres sites. Retour arrière : relancer avec le tag précédent (affiché à la fin). Nouveau domaine métier : `infra/deploy/add-domain-schema.sh <domaine>` d'abord.

## 5. Déployer mybook — demander

Suivre `~/mybook/docs/mode-operatoire-deploiement.md` (l'image est construite sur le serveur).

## 6. Keycloak — demander

- **Correctifs** : automatiques chaque nuit (`~/keycloak/auto-update.sh`, branche fixée dans `~/keycloak/docker-compose.yml`). En cas d'échec, le script revient seul à l'image précédente et note le digest fautif dans `.dernier-echec-digest` ; lire `~/keycloak/auto-update.log`.
- **Changer de branche** (26.8 → 26.9, 27…) : lire le guide de mise à jour officiel de Keycloak (changements de variables, de port de santé, de base), vérifier qu'une sauvegarde de la nuit existe, modifier la ligne `image:`, lancer `~/keycloak/auto-update.sh` à une heure creuse. Coupure de la connexion de tous les sites ≈ 1 min.
- **Thème** de la page de connexion : procédure dans `~/life-manager/docs/deploiement.md` (purger `/opt/keycloak/data/tmp/kc-gzip-cache`, sinon l'ancien CSS reste servi).
- Ne jamais exposer la console d'administration ni toucher aux realms sans Dorian.

## 7. Sauvegardes

- **Lister** (libre) : `ls -lt ~/infra/backups | head` ; OneDrive : `rclone lsl onedrive-chiffre:`.
- **Lancer à la main** (annoncer) : `~/infra/backup.sh` puis `~/infra/onedrive/send-backup.sh`.
- **Envoi OneDrive en échec** : lire `~/infra/onedrive/onedrive.log`. Jeton expiré → Dorian relance l'autorisation : `ssh -t -L 53682:127.0.0.1:53682 ubuntu@<serveur> 'rclone config reconnect onedrive:'` (lien à ouvrir dans son navigateur). Ne jamais le faire à sa place.
- **Restaurer** (demander, toujours) :
  1. Récupérer l'archive (`~/infra/backups` ou `rclone copy onedrive-chiffre:<archive> .`).
  2. `~/infra/restore.sh <archive>` : bases (comptes compris), volumes et configuration reviennent ensemble.
  3. `life-manager-files` doit appartenir à `65532:65532`.
  4. Redéployer Life Manager (`deploy.sh <dernier tag>`) et mybook, puis `etat.sh`.

## 8. Adresse IP bannie par fail2ban — demander

```bash
sudo fail2ban-client status sshd
sudo fail2ban-client set sshd unbanip <adresse>   # seulement l'adresse de Dorian, à sa demande
```

## 9. Plus d'accès SSH

Si la clé est refusée : Dorian passe par l'espace client OVH, soit la console KVM (si l'utilisateur a un mot de passe système), soit le **mode rescue** (redémarre sur un système de secours, disque monté à part), et vérifie `~/.ssh/authorized_keys` et `/etc/ssh/sshd_config.d/`. Ajouter une clé publique : `cat cle.pub >> ~/.ssh/authorized_keys` (à faire par Dorian).

## 10. Redémarrer le serveur — demander

Éviter 3 h – 5 h 30 UTC. `sudo systemctl reboot` ; au démarrage `vps-healthcheck` relance les conteneurs attendus et vérifie les sites. Ensuite : `etat.sh` et `tail /var/log/vps-maintenance.log`.

## 11. Mettre à jour cette documentation

Source : dépôt `life-manager`, dossier `infra/vps/admin/`. Après modification et commit, depuis le dépôt (Dorian, en `ubuntu`) :
```bash
scp infra/vps/admin/{AGENTS.md,CLAUDE.md} ubuntu@<serveur>:
scp infra/vps/admin/{INVENTAIRE.md,PROCEDURES.md,etat.sh} ubuntu@<serveur>:admin/
```
En `ia`, qui ne peut pas écrire dans `/home/ubuntu` : `ssh ia@<serveur> "sudo -u ubuntu tee /home/ubuntu/admin/<fichier> >/dev/null" < infra/vps/admin/<fichier>`.

## 12. Couper ou rétablir l'accès de l'IA — Dorian

```bash
sudo usermod -L -e 1 ia         # couper (compte verrouillé et expiré)
sudo usermod -U -e '' ia        # rétablir
```
`JOURNAL.md` vit **sur le serveur** (ne pas l'écraser) ; sa copie de départ est dans le dépôt.
