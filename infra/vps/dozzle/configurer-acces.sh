#!/usr/bin/env bash
# Configure l'accès à l'interface Docker, lancé PAR DORIAN une fois le realm Keycloak « infra » prêt :
#   ssh -t ubuntu@152.228.237.190 '~/dozzle/configurer-acces.sh'
# 1. adresse e-mail autorisée (celle de ton compte dans le realm infra) ;
# 2. secret du client « oauth2-proxy », enregistré dans ~/dozzle/.env sans être affiché.
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV="$DIR/.env"; EMAILS="$DIR/emails-autorises.txt"

read -rp "Adresse e-mail de ton compte Keycloak (realm infra) : " MAIL
[[ "$MAIL" == *@*.* ]] || { echo "Adresse invalide, rien changé."; exit 1; }
printf '# Adresses autorisées à entrer (une par ligne)\n%s\n' "$MAIL" > "$EMAILS"

read -rsp "Secret du client oauth2-proxy (Keycloak → realm infra → Clients → oauth2-proxy → Credentials) : " S; echo
[ -n "$S" ] || { echo "Secret vide, rien changé."; exit 1; }
touch "$ENV" && chmod 600 "$ENV"
grep -v '^OAUTH2_PROXY_CLIENT_SECRET=' "$ENV" > "$ENV.tmp" || true
printf 'OAUTH2_PROXY_CLIENT_SECRET=%s\n' "$S" >> "$ENV.tmp"
mv "$ENV.tmp" "$ENV" && chmod 600 "$ENV"
unset S
echo "C'est enregistré. Préviens Claude : il démarre et teste l'interface."
