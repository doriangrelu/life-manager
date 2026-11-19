#!/usr/bin/env bash
# Configuration unique de la copie des sauvegardes vers OneDrive, lancée PAR DORIAN depuis WSL :
#   ssh -t -L 53682:127.0.0.1:53682 ubuntu@152.228.237.190 '~/infra/onedrive/setup-onedrive.sh'
# Le tunnel (-L) permet d'autoriser rclone depuis le navigateur Windows.
# Crée deux « remotes » rclone : onedrive (accès au compte) et onedrive-chiffre (dossier Sauvegardes/VPS chiffré).
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if rclone listremotes | grep -qx 'onedrive:'; then
  echo "==> Accès OneDrive déjà configuré."
else
  echo "==> Étape 1 : autoriser rclone sur ton OneDrive."
  echo "    rclone va afficher un lien http://127.0.0.1:53682/auth?... : ouvre-le dans ton navigateur Windows,"
  echo "    connecte-toi à Microsoft et accepte. Reviens ensuite ici."
  rclone config create onedrive onedrive config_type=onedrive >/dev/null
  echo "==> Accès OneDrive enregistré."
fi

if rclone listremotes | grep -qx 'onedrive-chiffre:'; then
  echo "==> Chiffrement déjà configuré."
else
  echo "==> Étape 2 : phrase de chiffrement."
  echo "    Choisis une phrase longue et NOTE-LA dans ton gestionnaire de mots de passe :"
  echo "    sans elle, les sauvegardes sur OneDrive sont illisibles si le VPS disparaît."
  while true; do
    read -rsp "    Phrase : " P1; echo
    read -rsp "    Phrase (encore) : " P2; echo
    [ -n "$P1" ] && [ "$P1" = "$P2" ] && break
    echo "    Les deux saisies diffèrent ou sont vides, recommence."
  done
  rclone config create onedrive-chiffre crypt remote=onedrive:Sauvegardes/VPS password="$P1" --obscure >/dev/null
  unset P1 P2
  echo "==> Chiffrement enregistré."
fi

echo "==> Étape 3 : premier envoi."
"$DIR/send-backup.sh"

# Envoi chaque nuit à 3 h 30 UTC, après la sauvegarde de 3 h.
LINE="30 3 * * * $DIR/send-backup.sh >> $DIR/onedrive.log 2>&1"
if crontab -l 2>/dev/null | grep -qF "$DIR/send-backup.sh"; then
  echo "==> Tâche nocturne déjà en place."
else
  (crontab -l 2>/dev/null; echo "$LINE") | crontab -
  echo "==> Tâche nocturne ajoutée (3 h 30 UTC)."
fi
echo "==> Terminé. Rappel : garde bien la phrase de chiffrement en lieu sûr."
