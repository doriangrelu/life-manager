#!/usr/bin/env bash
# Maintenance nocturne du VPS (lancée par vps-maintenance.timer, en root).
#  1. met à jour tous les paquets (sans question, fichiers de configuration locaux conservés) ;
#  2. redémarre le serveur SEULEMENT si une mise à jour l'exige (noyau, libc…) ;
#  3. sinon vérifie tout de suite que les services répondent (vps-healthcheck.sh).
# Après un redémarrage, vps-healthcheck.service fait la vérification au démarrage.
# Journal : /var/log/vps-maintenance.log
set -euo pipefail

LOG=/var/log/vps-maintenance.log
STATE=/var/lib/vps-maintenance
exec >>"$LOG" 2>&1
log() { echo "$(date '+%F %T') maintenance : $*"; }

# Une seule maintenance à la fois.
exec 9>/run/vps-maintenance.lock
flock -n 9 || { log "déjà en cours, abandon"; exit 0; }

# Ne jamais couper la sauvegarde nocturne (~/infra/backup.sh, 3 h) : on attend qu'elle finisse (1 h au plus).
for _ in $(seq 1 60); do
    pgrep -f infra/backup.sh >/dev/null || break
    log "sauvegarde en cours, attente"
    sleep 60
done

log "début (noyau $(uname -r))"
mkdir -p "$STATE"
docker ps --format '{{.Names}}' | sort > "$STATE/running.txt"

export DEBIAN_FRONTEND=noninteractive
APT=(apt-get -q -y -o DPkg::Lock::Timeout=600 -o Dpkg::Options::=--force-confdef -o Dpkg::Options::=--force-confold)
"${APT[@]}" update
"${APT[@]}" full-upgrade
"${APT[@]}" autoremove --purge
log "paquets à jour ; restants : $(apt list --upgradable 2>/dev/null | grep -c upgradable || true)"

if [ -f /var/run/reboot-required ]; then
    log "redémarrage requis ($(tr '\n' ' ' < /var/run/reboot-required.pkgs 2>/dev/null)) : redémarrage"
    systemctl reboot
else
    log "pas de redémarrage nécessaire"
    /usr/local/sbin/vps-healthcheck.sh
fi
