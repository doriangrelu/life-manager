#!/usr/bin/env bash
# Enregistre l'adresse de signal Healthchecks.io sans l'afficher, puis envoie un premier signal.
# Lancé PAR DORIAN : ssh -t ubuntu@152.228.237.190 '~/admin/enregistrer-healthchecks.sh'
set -euo pipefail
F="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/.healthchecks-url"
read -rsp "Adresse de signal (Healthchecks.io → ta surveillance → « Ping URL », https://hc-ping.com/…) : " U; echo
[[ "$U" == https://hc-ping.com/* ]] || { echo "L'adresse doit commencer par https://hc-ping.com/ ; rien changé."; exit 1; }
umask 077; printf '%s\n' "$U" > "$F"; chmod 600 "$F"
C=$(curl -s -m 15 -o /dev/null -w '%{http_code}' --data-raw "Premier signal (enregistrement)" "$U")
unset U
[ "$C" = 200 ] && echo "Enregistré, premier signal reçu par Healthchecks.io (la surveillance doit passer au vert). Préviens Claude." \
               || echo "Enregistré, mais Healthchecks.io a répondu $C : vérifie l'adresse et relance."
