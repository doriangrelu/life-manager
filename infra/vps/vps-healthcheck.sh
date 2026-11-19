#!/usr/bin/env bash
# Vérifie que le serveur est revenu en état de marche (au démarrage, et après la maintenance) :
#  - vivacité : chaque conteneur qui tournait avant tourne de nouveau (relancé une fois s'il manque) ;
#  - disponibilité : conteneurs « healthy », API Life Manager « UP », sites publics qui répondent.
# Code de sortie 1 si quelque chose ne va pas (visible dans « systemctl status vps-healthcheck »).
# Journal : /var/log/vps-maintenance.log
set -uo pipefail

LOG=/var/log/vps-maintenance.log
STATE=/var/lib/vps-maintenance
# Sites publics à interroger (réponse 2xx ou 3xx attendue).
URLS="https://thierrylacassin-auteur.fr/ https://iabilis.fr/realms/life-manager/.well-known/openid-configuration https://maison.dorian-grelu.fr/"
# Points de santé internes, interrogés depuis le conteneur Caddy : « url|texte attendu ».
INTERNAL="http://life-manager-api:8080/health|UP"
CADDY=infra-caddy-1
WAIT_SECONDS=600

exec >>"$LOG" 2>&1
log() { echo "$(date '+%F %T') vérification : $*"; }

expected() { [ -s "$STATE/running.txt" ] && cat "$STATE/running.txt"; }
missing() { comm -23 <(expected | sort) <(docker ps --format '{{.Names}}' | sort); }
unhealthy() { docker ps --filter health=unhealthy --filter health=starting --format '{{.Names}}'; }
internal_ok() {
    local entry url text
    for entry in $INTERNAL; do
        url=${entry%%|*}; text=${entry#*|}
        docker exec "$CADDY" wget -qO- "$url" 2>/dev/null | grep -q "$text" || return 1
    done
}
sites_ko() {
    local url code
    for url in $URLS; do
        code=$(curl -s -o /dev/null -w '%{http_code}' --max-time 15 "$url")
        [[ "$code" =~ ^[23] ]] || echo "$url=$code"
    done
}

log "début (en marche depuis $(uptime -p))"
deadline=$((SECONDS + WAIT_SECONDS))
restarted=no
while :; do
    gone=$(missing | tr '\n' ' '); sick=$(unhealthy | tr '\n' ' ')
    if [ -z "${gone// }" ] && [ -z "${sick// }" ] && internal_ok && [ -z "$(sites_ko)" ]; then
        log "OK : $(docker ps -q | wc -l) conteneurs, services et sites disponibles"
        docker ps --format '{{.Names}}' | sort > "$STATE/running.txt"
        exit 0
    fi
    # Vivacité : un conteneur attendu absent depuis 2 min est relancé une fois.
    if [ -n "${gone// }" ] && [ "$restarted" = no ] && [ $((deadline - SECONDS)) -lt $((WAIT_SECONDS - 120)) ]; then
        log "conteneurs absents, relance : $gone"
        docker start $gone || true
        restarted=yes
    fi
    if [ "$SECONDS" -ge "$deadline" ]; then
        log "ÉCHEC après ${WAIT_SECONDS}s : absents [$gone] pas prêts [$sick] sites [$(sites_ko | tr '\n' ' ')]"
        exit 1
    fi
    sleep 10
done
