#!/usr/bin/env bash
# Crée le realm « life-manager » dans le Keycloak du socle (une seule fois), sans le redémarrer.
# Le secret du client life-manager-api vient de ~/life-manager/.env.prod.
# Demande l'identifiant et le mot de passe d'administration Keycloak (jamais enregistrés).
set -euo pipefail

REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="$REPO/.env.prod"
REALM_FILE="$REPO/infra/keycloak/realm-life-manager.prod.json"
KC_CONTAINER="${KC_CONTAINER:-keycloak-keycloak-1}"
KCADM=/opt/keycloak/bin/kcadm.sh

fail() { echo "ERREUR : $*" >&2; exit 1; }

[ -f "$ENV_FILE" ] || fail "$ENV_FILE absent : lancer d'abord install.sh."
SECRET="$(grep -E '^KEYCLOAK_API_CLIENT_SECRET=' "$ENV_FILE" | cut -d= -f2-)"
[ -n "$SECRET" ] || fail "KEYCLOAK_API_CLIENT_SECRET vide dans $ENV_FILE."

read -rp "Administrateur Keycloak : " KC_ADMIN
read -rsp "Mot de passe : " KC_PASSWORD; echo

# Session kcadm dans un fichier temporaire du conteneur, supprimé à la fin.
CONFIG=/tmp/kcadm-life-manager.config
trap 'docker exec "$KC_CONTAINER" rm -f "$CONFIG"' EXIT
kcadm() { docker exec -i "$KC_CONTAINER" "$KCADM" "$@" --config "$CONFIG"; }

# Mot de passe passé par variable d'environnement (« -e KC_PASSWORD » sans valeur) :
# il n'apparaît jamais dans la ligne de commande de l'hôte.
export KC_PASSWORD
docker exec -e KC_PASSWORD "$KC_CONTAINER" sh -c \
    "$KCADM config credentials --config $CONFIG --server http://localhost:8080 --realm master --user '$KC_ADMIN' --password \"\$KC_PASSWORD\"" >/dev/null
unset KC_PASSWORD

if kcadm get realms/life-manager >/dev/null 2>&1; then
    fail "le realm life-manager existe déjà : rien n'est modifié."
fi

echo "==> Import du realm life-manager"
sed "s/__KEYCLOAK_API_CLIENT_SECRET__/$SECRET/" "$REALM_FILE" | kcadm create realms -f -
echo "==> Realm créé. Ajouter les comptes du foyer dans la console (realm life-manager > Users)."
