#!/usr/bin/env bash
# Installe (ou met à jour) la maintenance nocturne du VPS. À lancer sur le VPS depuis le dépôt :
#   infra/vps/install-maintenance.sh
# Effet : scripts dans /usr/local/sbin, unités systemd, minuterie activée (5 h), vérification à chaque démarrage.
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

sudo install -m 755 "$DIR/vps-maintenance.sh" "$DIR/vps-healthcheck.sh" /usr/local/sbin/
sudo install -m 644 "$DIR"/systemd/vps-maintenance.service "$DIR"/systemd/vps-maintenance.timer \
    "$DIR"/systemd/vps-healthcheck.service /etc/systemd/system/
sudo install -d -m 755 /var/lib/vps-maintenance
# Liste des conteneurs attendus : ceux qui tournent maintenant.
docker ps --format '{{.Names}}' | sort | sudo tee /var/lib/vps-maintenance/running.txt >/dev/null
sudo touch /var/log/vps-maintenance.log

# Journal gardé 8 semaines.
sudo tee /etc/logrotate.d/vps-maintenance >/dev/null <<'EOF'
/var/log/vps-maintenance.log {
    weekly
    rotate 8
    compress
    missingok
    notifempty
}
EOF

sudo systemctl daemon-reload
sudo systemctl enable --now vps-maintenance.timer
sudo systemctl enable vps-healthcheck.service
systemctl list-timers vps-maintenance.timer --no-pager
echo "==> Installé. Journal : /var/log/vps-maintenance.log ; essai à la main : sudo systemctl start vps-healthcheck"
