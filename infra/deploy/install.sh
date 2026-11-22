#!/usr/bin/env bash
# Première installation de Life Manager sur le VPS (une seule fois).
# À lancer depuis le dépôt cloné dans ~/life-manager : infra/deploy/install.sh
# Crée le dossier des pièces jointes et génère les secrets dans ~/life-manager/.env.prod (droits 600).
# Si le realm a été importé à la main, passer son secret client :
#   KEYCLOAK_API_CLIENT_SECRET=<secret du realm> infra/deploy/install.sh
# Ne touche à aucune autre application du serveur.
set -euo pipefail

REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="$REPO/.env.prod"
FILES_DIR="${FILES_DIR:-$HOME/volumes/life-manager-files}"
# Utilisateur de l'image du backend (distroless « nonroot »).
API_UID=65532

fail() { echo "ERREUR : $*" >&2; exit 1; }

command -v docker >/dev/null || fail "Docker est absent."
docker network inspect socle >/dev/null 2>&1 || fail "le réseau Docker « socle » n'existe pas (socle non installé ?)."
[ -e "$ENV_FILE" ] && fail "$ENV_FILE existe déjà : installation déjà faite, rien n'est modifié."

secret() { openssl rand -hex 24; }

echo "==> Génération des secrets dans $ENV_FILE"
umask 077
cat > "$ENV_FILE" <<EOF
# Secrets de production de Life Manager. Ne jamais commiter ni copier ailleurs que dans les sauvegardes.
LIFE_MANAGER_TAG=
FILES_DIR=$FILES_DIR
KEYCLOAK_API_CLIENT_SECRET=${KEYCLOAK_API_CLIENT_SECRET:-$(secret)}
IDENTITY_DB_PASSWORD=$(secret)
HOUSEHOLD_DB_PASSWORD=$(secret)
BUDGET_DB_PASSWORD=$(secret)
VEHICLE_DB_PASSWORD=$(secret)
SHOPPING_DB_PASSWORD=$(secret)
REMINDER_DB_PASSWORD=$(secret)
BILLS_DB_PASSWORD=$(secret)
PROJECT_DB_PASSWORD=$(secret)
EOF
chmod 600 "$ENV_FILE"

echo "==> Dossier des pièces jointes : $FILES_DIR"
sudo mkdir -p "$FILES_DIR"
sudo chown "$API_UID:$API_UID" "$FILES_DIR"
sudo chmod 700 "$FILES_DIR"

cat <<EOF
==> Installation terminée. Étapes suivantes :
    infra/deploy/create-database.sh
    infra/deploy/create-realm.sh
    infra/deploy/deploy.sh <tag>
EOF
