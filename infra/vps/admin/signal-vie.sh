#!/usr/bin/env bash
# Signal de vie vers Healthchecks.io (lancé toutes les 10 min par vps-signal-vie.timer, en « ubuntu »).
# Lance etat.sh : OK → signal de réussite ; écart → nouvel essai 2 min plus tard (évite les fausses
# alertes pendant un redémarrage court, ex. Keycloak à 4 h 15), puis signal d'échec avec le détail.
# Si plus aucun signal n'arrive (serveur éteint), Healthchecks.io prévient de lui-même.
# Adresse de signal : ~/admin/.healthchecks-url (droits 600, enregistrée par enregistrer-healthchecks.sh).
# Essai à la main : ~/admin/signal-vie.sh --test-echec (envoie un échec volontaire).
set -uo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
URL_FILE="$DIR/.healthchecks-url"
[ -s "$URL_FILE" ] || { echo "Adresse de signal absente : $URL_FILE"; exit 1; }
URL=$(<"$URL_FILE")

send() { # $1 = suffixe ("" ou "/fail"), $2 = texte joint
  curl -fsS -m 15 --retry 3 -o /dev/null --data-raw "$2" "$URL$1" || echo "envoi du signal impossible"
}

if [ "${1:-}" = "--test-echec" ]; then
  send /fail "TEST volontaire d'alerte lancé à la main le $(date -u '+%F %H:%M') UTC : rien de grave."
  echo "échec de test envoyé"; exit 0
fi

OUT=$("$DIR/etat.sh" 2>&1); CODE=$?
if [ $CODE -ne 0 ]; then
  sleep 120
  OUT=$("$DIR/etat.sh" 2>&1); CODE=$?
fi
if [ $CODE -eq 0 ]; then
  send "" "$(echo "$OUT" | tail -1)"
else
  # Les écarts d'abord, l'état complet ensuite (visible dans le message d'alerte)
  send /fail "$(echo "$OUT" | sed -n '/^RÉSULTAT/,$p'; echo; echo "$OUT")"
fi
exit $CODE
