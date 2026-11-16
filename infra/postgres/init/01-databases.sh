#!/usr/bin/env bash
# Crée la base applicative (un schéma et un compte par domaine) et la base Keycloak.
# Chaque compte ne voit que son schéma : les domaines restent séparés jusque dans la base.
set -euo pipefail

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<SQL
CREATE ROLE keycloak LOGIN PASSWORD '${KEYCLOAK_DB_PASSWORD}';
CREATE DATABASE keycloak OWNER keycloak;

CREATE ROLE identity_app LOGIN PASSWORD '${IDENTITY_DB_PASSWORD}';
CREATE ROLE household_app LOGIN PASSWORD '${HOUSEHOLD_DB_PASSWORD}';
CREATE ROLE budget_app LOGIN PASSWORD '${BUDGET_DB_PASSWORD}';
CREATE ROLE vehicle_app LOGIN PASSWORD '${VEHICLE_DB_PASSWORD}';
CREATE ROLE shopping_app LOGIN PASSWORD '${SHOPPING_DB_PASSWORD}';
CREATE ROLE reminder_app LOGIN PASSWORD '${REMINDER_DB_PASSWORD}';
CREATE DATABASE lifemanager;
SQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname lifemanager <<SQL
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
