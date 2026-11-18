#!/usr/bin/env bash
# Crée la base « lifemanager » dans le PostgreSQL du socle (une seule fois) :
# un compte et un schéma par domaine métier, comme en développement (infra/postgres/init).
# Les mots de passe viennent de ~/life-manager/.env.prod ; les autres bases ne sont pas touchées.
set -euo pipefail

REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="$REPO/.env.prod"
SOCLE_ENV="${SOCLE_ENV:-$HOME/infra/.env}"
DB_CONTAINER="${DB_CONTAINER:-infra-db-1}"

fail() { echo "ERREUR : $*" >&2; exit 1; }

[ -f "$ENV_FILE" ] || fail "$ENV_FILE absent : lancer d'abord install.sh."
[ -f "$SOCLE_ENV" ] || fail "$SOCLE_ENV absent : compte administrateur du socle introuvable."
# Seul le nom du compte administrateur est lu dans le .env du socle.
ADMIN="$(grep -E '^POSTGRES_USER=' "$SOCLE_ENV" | cut -d= -f2-)"
[ -n "$ADMIN" ] || fail "POSTGRES_USER introuvable dans $SOCLE_ENV."
# shellcheck source=/dev/null
set -a; . "$ENV_FILE"; set +a

psql_admin() { docker exec -i "$DB_CONTAINER" psql -v ON_ERROR_STOP=1 -U "$ADMIN" "$@"; }

if [ "$(psql_admin -d postgres -Atc "SELECT 1 FROM pg_database WHERE datname = 'lifemanager'")" = "1" ]; then
    fail "la base lifemanager existe déjà : rien n'est modifié."
fi

echo "==> Comptes et base lifemanager"
psql_admin -d postgres <<SQL
CREATE ROLE identity_app LOGIN PASSWORD '${IDENTITY_DB_PASSWORD}';
CREATE ROLE household_app LOGIN PASSWORD '${HOUSEHOLD_DB_PASSWORD}';
CREATE ROLE budget_app LOGIN PASSWORD '${BUDGET_DB_PASSWORD}';
CREATE ROLE vehicle_app LOGIN PASSWORD '${VEHICLE_DB_PASSWORD}';
CREATE ROLE shopping_app LOGIN PASSWORD '${SHOPPING_DB_PASSWORD}';
CREATE ROLE reminder_app LOGIN PASSWORD '${REMINDER_DB_PASSWORD}';
CREATE DATABASE lifemanager;
REVOKE ALL ON DATABASE lifemanager FROM PUBLIC;
SQL

echo "==> Un schéma par domaine"
psql_admin -d lifemanager <<SQL
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT CONNECT ON DATABASE lifemanager TO identity_app, household_app, budget_app, vehicle_app, shopping_app, reminder_app;

CREATE SCHEMA identity AUTHORIZATION identity_app;
CREATE SCHEMA household AUTHORIZATION household_app;
CREATE SCHEMA budget AUTHORIZATION budget_app;
CREATE SCHEMA vehicle AUTHORIZATION vehicle_app;
CREATE SCHEMA shopping AUTHORIZATION shopping_app;
CREATE SCHEMA reminder AUTHORIZATION reminder_app;

-- Chaque compte travaille par défaut dans son propre schéma.
ALTER ROLE identity_app SET search_path = identity;
ALTER ROLE household_app SET search_path = household;
ALTER ROLE budget_app SET search_path = budget;
ALTER ROLE vehicle_app SET search_path = vehicle;
ALTER ROLE shopping_app SET search_path = shopping;
ALTER ROLE reminder_app SET search_path = reminder;
SQL

echo "==> Base lifemanager créée."
