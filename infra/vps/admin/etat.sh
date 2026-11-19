#!/usr/bin/env bash
# État du serveur en un coup d'œil (lecture seule, aucun secret).
# Code de sortie 1 s'il y a au moins un écart, listé à la fin.
set -uo pipefail
PB=()
ko() { PB+=("$*"); }
age_h() { echo $(( ($(date +%s) - $(stat -c %Y "$1")) / 3600 )); }

echo "== Serveur : $(hostname), $(date -u '+%F %H:%M') UTC, en marche $(uptime -p | sed 's/up //')"
D=$(df --output=pcent / | tail -1 | tr -dc 0-9)
M=$(free -m | awk '/^Mem/ {print int($7*100/$2)}')
echo "   disque : ${D} % utilisé | mémoire disponible : ${M} % | charge : $(cut -d' ' -f1-3 /proc/loadavg)"
[ "$D" -ge 80 ] && ko "disque à ${D} % (procédure 3)"
[ "$M" -le 10 ] && ko "mémoire disponible à ${M} %"
[ -f /var/run/reboot-required ] && echo "   redémarrage demandé par une mise à jour (sera fait à 5 h UTC)"

echo "== Conteneurs"
EXPECTED=/var/lib/vps-maintenance/running.txt
docker ps -a --format '   {{.Names}} | {{.Image}} | {{.Status}}' | sort
if [ -s "$EXPECTED" ]; then
  for c in $(comm -23 <(sort "$EXPECTED") <(docker ps --format '{{.Names}}' | sort)); do ko "conteneur attendu arrêté : $c (procédure 2)"; done
fi
for c in $(docker ps --filter health=unhealthy --format '{{.Names}}'); do ko "conteneur en mauvaise santé : $c"; done
for c in $(docker ps -a --filter status=restarting --format '{{.Names}}'); do ko "conteneur qui redémarre en boucle : $c"; done

echo "== Sites"
for u in https://maison.dorian-grelu.fr/ https://thierrylacassin-auteur.fr/ https://leblogdethierry.fr/ \
         https://docker.dorian-grelu.fr/ https://metrics.dorian-grelu.fr/ \
         https://iabilis.fr/realms/life-manager/.well-known/openid-configuration; do
  c=$(curl -s -o /dev/null -m 15 -w '%{http_code}' "$u")
  echo "   $c  $u"; [[ "$c" =~ ^[23] ]] || ko "site en erreur : $u ($c)"
done
API=$(docker exec infra-caddy-1 wget -qO- http://life-manager-api:8080/health </dev/null 2>/dev/null | grep -o UP)
echo "   API Life Manager : ${API:-KO}"; [ "$API" = UP ] || ko "API Life Manager pas UP"
END=$(echo | openssl s_client -connect maison.dorian-grelu.fr:443 -servername maison.dorian-grelu.fr 2>/dev/null | openssl x509 -noout -enddate 2>/dev/null | cut -d= -f2)
[ -n "$END" ] && echo "   certificat maison.dorian-grelu.fr valable jusqu'au : $END"

echo "== Sauvegardes"
L=$(ls -t ~/infra/backups/sauvegarde-*.tar.gz 2>/dev/null | head -1)
if [ -n "$L" ]; then
  A=$(age_h "$L"); echo "   dernière locale : $(basename "$L") ($(du -h "$L" | cut -f1)), il y a ${A} h ; $(ls ~/infra/backups/sauvegarde-*.tar.gz | wc -l) gardées"
  [ "$A" -gt 26 ] && ko "dernière sauvegarde locale vieille de ${A} h"
else ko "aucune sauvegarde locale"; fi
O=~/infra/onedrive/onedrive.log
if [ -f "$O" ]; then
  echo "   OneDrive : $(tail -1 "$O")"
  [ "$(age_h "$O")" -gt 26 ] && ko "copie OneDrive pas faite depuis plus de 26 h"
  tail -1 "$O" | grep -q 'envoi : OK' || ko "dernier envoi OneDrive en échec (procédure 7)"
else echo "   OneDrive : pas encore de journal (premier envoi automatique à 3 h 30 UTC)"; fi

echo "== Tâches nocturnes"
echo "   Keycloak : $(grep -E '^=== .*(reussie|ANNULEE|verification)' ~/keycloak/auto-update.log 2>/dev/null | tail -1)"
grep -q CRITIQUE <(tail -30 ~/keycloak/auto-update.log 2>/dev/null) && ko "auto-update Keycloak : CRITIQUE dans le journal"
echo "   maintenance : $(tail -1 /var/log/vps-maintenance.log 2>/dev/null)"
tail -1 /var/log/vps-maintenance.log 2>/dev/null | grep -q 'ÉCHEC' && ko "dernière vérification de maintenance en échec"

echo "== Sécurité"
echo "   fail2ban sshd : $(sudo -n fail2ban-client status sshd 2>/dev/null | awk -F: '/Currently banned/ {gsub(/ /,"",$2); print $2}') adresse(s) bannie(s)"
P=$(sudo -n sshd -T 2>/dev/null | awk '/^passwordauthentication/ {print $2}')
echo "   SSH par mot de passe : ${P:-?}"; [ "$P" = no ] || ko "SSH accepte les mots de passe"
sudo -n ufw status 2>/dev/null | grep -q 'Status: active' || ko "pare-feu ufw inactif"

echo
if [ ${#PB[@]} -eq 0 ]; then echo "RÉSULTAT : OK"; exit 0; fi
echo "RÉSULTAT : ${#PB[@]} écart(s)"; printf '  - %s\n' "${PB[@]}"; exit 1
