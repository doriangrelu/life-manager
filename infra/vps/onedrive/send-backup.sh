#!/usr/bin/env bash
# Copie les archives de ~/infra/backups vers OneDrive (chiffrées), puis supprime celles de plus de 30 jours.
# Lancé chaque nuit par cron ; journal : ~/infra/onedrive/onedrive.log
set -euo pipefail
SRC="$HOME/infra/backups"
DEST="onedrive-chiffre:"
FILTER=(--include 'sauvegarde-*.tar.gz')

echo "$(date -u '+%F %T') envoi : début"
# copy n'envoie que les archives absentes de OneDrive
rclone copy "$SRC" "$DEST" "${FILTER[@]}" --stats-one-line --stats 0
rclone delete "$DEST" "${FILTER[@]}" --min-age 30d
echo "$(date -u '+%F %T') envoi : OK, $(rclone lsf "$DEST" "${FILTER[@]}" | wc -l) archives sur OneDrive"
