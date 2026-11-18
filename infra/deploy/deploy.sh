#!/usr/bin/env bash
# Déploie une version publiée de Life Manager sur le VPS : infra/deploy/deploy.sh v0.1.0
# Retour arrière : relancer avec le tag précédent (affiché à la fin de chaque déploiement).
#
# 1. Place le dépôt sur le tag (scripts, compose et routage de cette version).
# 2. Tire les images du tag et redémarre l'API et la PWA.
# 3. Attend que l'API réponde « UP », puis installe le routage dans le Caddy du socle.
set -euo pipefail

# Tout le script est dans main() : bash le lit en entier avant de changer de version du dépôt.
main() {
    local tag="${1:-}"
    [ -n "$tag" ] || fail "usage : deploy.sh <tag>   (ex. deploy.sh v0.1.0)"

    REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
    ENV_FILE="$REPO/.env.prod"
    CADDY="${CADDY_CONTAINER:-infra-caddy-1}"
    CONF_D="${CADDY_CONF_D:-$HOME/infra/conf.d}"
    [ -f "$ENV_FILE" ] || fail "$ENV_FILE absent : lancer d'abord install.sh."

    checkout "$tag"
    local previous
    previous="$(grep -E '^LIFE_MANAGER_TAG=' "$ENV_FILE" | cut -d= -f2-)"
    set_tag "$tag"

    echo "==> Images $tag"
    # SKIP_PULL=1 : images déjà présentes (essai local sans registre).
    [ "${SKIP_PULL:-0}" = 1 ] || compose pull
    compose up -d

    wait_until_up http://life-manager-api:8080/health '"status":"UP"' || rollback_hint "$previous"
    wait_until_up http://life-manager-web:8080/ '<title>' || rollback_hint "$previous"
    install_route

    echo "==> Life Manager $tag déployé.${previous:+ Retour arrière : infra/deploy/deploy.sh $previous}"
}

fail() { echo "ERREUR : $*" >&2; exit 1; }

compose() {
    # Nom de projet distinct de celui du développement (« life-manager ») : aucun risque de toucher l'un depuis l'autre.
    docker compose -p life-manager-prod --env-file "$ENV_FILE" -f "$REPO/infra/deploy/docker-compose.prod.yml" "$@"
}

checkout() {
    echo "==> Dépôt sur $1"
    git -C "$REPO" fetch --quiet --tags origin
    git -C "$REPO" diff --quiet || fail "le dépôt a des modifications locales : rien n'est déployé."
    git -C "$REPO" -c advice.detachedHead=false checkout --quiet "$1"
}

set_tag() {
    sed -i "s/^LIFE_MANAGER_TAG=.*/LIFE_MANAGER_TAG=$1/" "$ENV_FILE"
}

# Appel depuis le conteneur Caddy, seul à voir le réseau « socle » (l'image de l'API n'a pas de shell).
wait_until_up() {
    local url="$1" expected="$2"
    echo "==> Attente de $url"
    for _ in $(seq 1 60); do
        if docker exec "$CADDY" wget -qO- "$url" 2>/dev/null | grep -q "$expected"; then
            return 0
        fi
        sleep 2
    done
    return 1
}

rollback_hint() {
    compose logs --tail 30 api >&2 || true
    fail "le service ne répond pas.${1:+ Revenir à la version précédente : infra/deploy/deploy.sh $1}"
}

# Copie le routage puis recharge Caddy ; en cas d'erreur, l'ancien routage est remis.
install_route() {
    echo "==> Routage Caddy"
    local target="$CONF_D/life-manager.caddy" backup
    backup="$(mktemp)"
    [ -f "$target" ] && cp "$target" "$backup"
    cp "$REPO/infra/caddy/life-manager.caddy" "$target"
    if ! docker exec "$CADDY" caddy reload --config /etc/caddy/Caddyfile; then
        if [ -s "$backup" ]; then cp "$backup" "$target"; else rm -f "$target"; fi
        docker exec "$CADDY" caddy reload --config /etc/caddy/Caddyfile || true
        fail "routage Caddy refusé : l'ancien routage est remis."
    fi
    rm -f "$backup"
}

main "$@"
