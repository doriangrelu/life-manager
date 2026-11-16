#!/usr/bin/env bash
# Crée deux comptes de test dans le realm life-manager (environnement de dev uniquement).
# Usage : ./infra/keycloak/create-dev-users.sh   (Keycloak doit tourner via docker compose)
#
# Comptes créés (mot de passe commun : dev-password) :
#   alice@example.org  — Alice Martin
#   bob@example.org    — Bob Durand
set -euo pipefail
cd "$(dirname "$0")/../.."

docker compose exec -T keycloak bash -s <<'EOF'
set -e
KCADM=/opt/keycloak/bin/kcadm.sh
$KCADM config credentials --server http://localhost:8180 --realm master \
  --user "${KC_BOOTSTRAP_ADMIN_USERNAME}" --password "${KC_BOOTSTRAP_ADMIN_PASSWORD}" >/dev/null

create_user() { # email prénom nom
  if [ -n "$($KCADM get users -r life-manager -q email="$1" --fields id --format csv --noquotes)" ]; then
    echo "déjà présent : $1"
    return
  fi
  $KCADM create users -r life-manager -s username="$1" -s email="$1" -s firstName="$2" -s lastName="$3" \
    -s emailVerified=true -s enabled=true
  $KCADM set-password -r life-manager --username "$1" --new-password dev-password
  echo "créé : $1"
}

create_user alice@example.org Alice Martin
create_user bob@example.org Bob Durand
EOF
