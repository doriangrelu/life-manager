#!/usr/bin/env bash
# Ajoute le compte et le schéma d'un nouveau domaine à une base lifemanager déjà créée.
# Exemple : infra/deploy/add-domain-schema.sh bills
# Sans effet si tout existe déjà : peut être relancé.
#  - génère <DOMAINE>_DB_PASSWORD dans ~/life-manager/.env.prod s'il manque ;
#  - crée le compte <domaine>_app et le schéma <domaine> s'ils manquent.
# À lancer AVANT deploy.sh de la version qui apporte le domaine.
set -euo pipefail

DOMAIN="${1:-}"
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="$REPO/.env.prod"
SOCLE_ENV="${SOCLE_ENV:-$HOME/infra/.env}"
DB_CONTAINER="${DB_CONTAINER:-infra-db-1}"

fail() { echo "ERREUR : $*" >&2; exit 1; }

[[ "$DOMAIN" =~ ^[a-z]+$ ]] || fail "usage : $0 <domaine en minuscules, ex. bills>"
[ -f "$ENV_FILE" ] || fail "$ENV_FILE absent : lancer d'abord install.sh."
[ -f "$SOCLE_ENV" ] || fail "$SOCLE_ENV absent : compte administrateur du socle introuvable."
ADMIN="$(grep -E '^POSTGRES_USER=' "$SOCLE_ENV" | cut -d= -f2-)"
[ -n "$ADMIN" ] || fail "POSTGRES_USER introuvable dans $SOCLE_ENV."

VAR="$(echo "$DOMAIN" | tr '[:lower:]' '[:upper:]')_DB_PASSWORD"
ROLE="${DOMAIN}_app"

if ! grep -qE "^${VAR}=" "$ENV_FILE"; then
    echo "==> Secret $VAR ajouté à $ENV_FILE"
    echo "${VAR}=$(openssl rand -hex 24)" >> "$ENV_FILE"
fi
PASSWORD="$(grep -E "^${VAR}=" "$ENV_FILE" | cut -d= -f2-)"

psql_admin() { docker exec -i "$DB_CONTAINER" psql -v ON_ERROR_STOP=1 -U "$ADMIN" "$@"; }

if [ "$(psql_admin -d postgres -Atc "SELECT 1 FROM pg_roles WHERE rolname = '$ROLE'")" != "1" ]; then
    echo "==> Compte $ROLE"
    psql_admin -d postgres -q <<SQL
CREATE ROLE $ROLE LOGIN PASSWORD '$PASSWORD';
ALTER ROLE $ROLE SET search_path = $DOMAIN;
SQL
fi

echo "==> Schéma $DOMAIN"
psql_admin -d lifemanager -q <<SQL
GRANT CONNECT ON DATABASE lifemanager TO $ROLE;
CREATE SCHEMA IF NOT EXISTS $DOMAIN AUTHORIZATION $ROLE;
SQL

echo "==> Domaine $DOMAIN prêt."
