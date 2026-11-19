#!/usr/bin/env bash
# Parcours de vérification de l'exécutable natif : un vrai scénario d'usage sur l'API compilée par GraalVM.
# Attrape ce que les tests JVM ne voient pas (réflexion, ressources, classes chargées dynamiquement…).
#
# Prérequis : PostgreSQL et Keycloak de développement lancés (docker compose up -d postgres keycloak),
# comptes de test créés (infra/keycloak/create-dev-users.sh), API native joignable sur $API.
# Usage : API=http://localhost:8081 infra/ci/native-smoke.sh
set -Eeuo pipefail
# Toute erreur devient une annotation GitHub, lisible sans ouvrir les journaux.
trap 'echo "::error::native-smoke : échec ligne $LINENO : $BASH_COMMAND"' ERR

API="${API:-http://localhost:8081}"
KEYCLOAK="${KEYCLOAK:-http://localhost:8180}"
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
WORK="$(mktemp -d)"
STEP=0

fail() { echo "ÉCHEC : $*" >&2; echo "::error::native-smoke : $*"; exit 1; }
json() { python3 -c "import json,sys; print(json.load(sys.stdin)$1)"; }

# Jeton par mot de passe : autorisé sur le client de la PWA le temps du test uniquement.
kcadm() { docker compose -f "$REPO/docker-compose.yml" exec -T keycloak /opt/keycloak/bin/kcadm.sh "$@"; }
kcadm config credentials --server http://localhost:8180 --realm master --user admin --password admin >/dev/null
PWA_CLIENT=$(kcadm get clients -r life-manager -q clientId=life-manager-pwa --fields id --format csv --noquotes)
kcadm update "clients/$PWA_CLIENT" -r life-manager -s directAccessGrantsEnabled=true
trap 'kcadm update "clients/$PWA_CLIENT" -r life-manager -s directAccessGrantsEnabled=false; rm -rf "$WORK"' EXIT

token() {
  curl -sSf -d grant_type=password -d client_id=life-manager-pwa -d scope=openid -d "username=$1" -d password=dev-password \
    "$KEYCLOAK/realms/life-manager/protocol/openid-connect/token" | json "['access_token']"
}
ALICE=$(token alice@example.org)
BOB=$(token bob@example.org)

# Appel de l'API : vrai si le code HTTP est celui attendu ; la réponse est gardée dans $WORK/body.
attempt() { # jeton méthode chemin code-attendu [corps-json]
  local token=$1 method=$2 path=$3 expected=$4 body=${5:-}
  LAST_CODE=$(curl -s -m 20 -o "$WORK/body" -w '%{http_code}' -X "$method" -H "Authorization: Bearer $token" \
    ${body:+-H 'Content-Type: application/json' -d "$body"} "$API/api$path")
  [ "$LAST_CODE" = "$expected" ]
}

# Même appel, qui arrête le parcours en cas d'écart.
call() {
  STEP=$((STEP + 1))
  attempt "$@" || fail "étape $STEP : $2 $3 → $LAST_CODE (attendu $4) : $(head -c 300 "$WORK/body")"
  echo "ok $STEP : $2 $3 → $LAST_CODE"
}
body() { json "$1" < "$WORK/body"; }

# Les copies locales des membres arrivent par événement : un appel qui en dépend a quelques secondes pour réussir.
# Il n'est rejoué que s'il a échoué (refusé, donc rien n'a été créé).
eventually() { # nombre-d-essais call …
  local tries=$1; shift; shift
  STEP=$((STEP + 1))
  for _ in $(seq 1 "$tries"); do
    if attempt "$@"; then
      echo "ok $STEP : $2 $3 → $LAST_CODE"
      return 0
    fi
    sleep 1
  done
  fail "étape $STEP : $2 $3 → $LAST_CODE (attendu $4) : $(head -c 300 "$WORK/body")"
}

TODAY=$(date +%F)
MONTH=$(date +%Y-%m)

echo "== Identité et foyer"
# Comme la PWA au démarrage : le premier appel crée la fiche de l'utilisateur, transmise aux autres domaines par événement.
# Keycloak peut finir de démarrer après l'API : l'introspection des jetons répond alors 503 quelques secondes.
eventually 30 call "$BOB" GET /whoami 200
BOB_ID=$(body "['userId']")
eventually 30 call "$ALICE" GET /whoami 200
ALICE_ID=$(body "['userId']")
eventually 10 call "$ALICE" POST /households 201 '{"name":"  Foyer   NATIF "}'
HOUSEHOLD=$(body "['id']")
call "$ALICE" GET "/households/$HOUSEHOLD" 200
[ "$(body "['name']")" = "foyer natif" ] || fail "nom de foyer non normalisé : $(body "['name']")"

echo "== Invitation (limite d'essais en cache Caffeine)"
call "$ALICE" POST "/households/$HOUSEHOLD/invitations" 201
CODE=$(body "['code']")
call "$BOB" POST /households/join 400 '{"code":"ZZZZZZZZ"}'
eventually 10 call "$BOB" POST /households/join 200 "{\"code\":\"$CODE\"}"

echo "== Budget"
eventually 10 call "$ALICE" GET "/households/$HOUSEHOLD/expense-categories" 200
CATEGORY=$(body "[0]['id']")
eventually 10 call "$ALICE" POST "/households/$HOUSEHOLD/expenses" 201 \
  "{\"amountCents\":1234,\"categoryId\":\"$CATEGORY\",\"newCategoryName\":null,\"spentOn\":\"$TODAY\",\"paidBy\":\"$ALICE_ID\",\"beneficiary\":null,\"label\":\"Essai\"}"
call "$ALICE" GET "/households/$HOUSEHOLD/budget/$MONTH" 200
[ "$(body "['totalCents']")" = "1234" ] || fail "total du mois inattendu : $(head -c 400 "$WORK/body")"

echo "== Courses"
eventually 10 call "$BOB" GET "/households/$HOUSEHOLD/shopping-lists" 200
LIST=$(body "[0]['id']")
call "$BOB" POST "/households/$HOUSEHOLD/shopping-lists/$LIST/items" 201 '{"label":"Lait"}'
ITEM=$(body "['id']")
call "$BOB" POST "/households/$HOUSEHOLD/shopping-lists/$LIST/items/$ITEM/check" 200
call "$BOB" POST "/households/$HOUSEHOLD/shopping-lists/$LIST/clear-checked" 204
call "$BOB" GET "/households/$HOUSEHOLD/shopping-lists/$LIST/history" 200

echo "== Véhicule, carnet et pièce jointe (Tika)"
eventually 10 call "$ALICE" POST "/households/$HOUSEHOLD/vehicles" 201 \
  '{"name":"Clio","licensePlate":"AB-123-CD","technicalInspectionDue":null,"serviceDue":null}'
VEHICLE=$(body "['id']")
call "$ALICE" POST "/households/$HOUSEHOLD/vehicles/$VEHICLE/maintenance" 201 \
  "{\"performedOn\":\"$TODAY\",\"kind\":\"TECHNICAL_INSPECTION\",\"mileageKm\":42000,\"costCents\":7900,\"note\":null,\"nextDueOn\":null}"
ENTRY=$(body "['id']")
printf '%%PDF-1.4\n1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n2 0 obj<</Type/Pages/Kids[]/Count 0>>endobj\ntrailer<</Root 1 0 R>>\n%%%%EOF\n' > "$WORK/facture.pdf"
STEP=$((STEP + 1))
code=$(curl -s -o "$WORK/body" -w '%{http_code}' -H "Authorization: Bearer $ALICE" -F "file=@$WORK/facture.pdf;type=application/pdf" \
  "$API/api/households/$HOUSEHOLD/vehicles/$VEHICLE/maintenance/$ENTRY/attachments")
[ "$code" = 201 ] || fail "étape $STEP : envoi du PDF → $code : $(head -c 300 "$WORK/body")"
echo "ok $STEP : envoi d'un PDF → 201"
ATTACHMENT=$(body "['id']")
STEP=$((STEP + 1))
curl -sf -H "Authorization: Bearer $ALICE" -o "$WORK/retour.pdf" \
  "$API/api/households/$HOUSEHOLD/vehicles/$VEHICLE/maintenance/$ENTRY/attachments/$ATTACHMENT/content"
cmp -s "$WORK/facture.pdf" "$WORK/retour.pdf" || fail "étape $STEP : fichier téléchargé différent"
echo "ok $STEP : téléchargement identique"

echo "== Charges : fixe payée seule, variable confirmée, facture"
charge() { # nom montant FIXED|VARIABLE
  echo "{\"name\":\"$1\",\"amountCents\":$2,\"amountKind\":\"$3\",\"frequency\":\"MONTHLY\",\"firstDueOn\":\"$TODAY\",\"budgetCategoryId\":\"$CATEGORY\",\"payerId\":\"$ALICE_ID\",\"beneficiaryId\":null}"
}
eventually 10 call "$ALICE" POST "/households/$HOUSEHOLD/charges" 201 "$(charge Internet 3999 FIXED)"
eventually 10 call "$ALICE" POST "/households/$HOUSEHOLD/charges" 201 "$(charge Electricite 8500 VARIABLE)"
CHARGE=$(body "['id']")
# Échéance du jour d'une charge variable : elle attend le montant de la facture.
[ "$(body "['next']['status']")" = "TO_CONFIRM" ] || fail "échéance variable non à confirmer : $(head -c 400 "$WORK/body")"
OCCURRENCE=$(body "['next']['id']")
call "$ALICE" GET "/households/$HOUSEHOLD/charges/$CHARGE/occurrences" 200
call "$ALICE" POST "/households/$HOUSEHOLD/charges/$CHARGE/occurrences/$OCCURRENCE/confirm" 200 '{"amountCents":9240}'
# 12,34 € saisis + 39,99 € (fixe) + 92,40 € (variable confirmée).
eventually 10 call "$ALICE" GET "/households/$HOUSEHOLD/budget/$MONTH" 200
[ "$(body "['totalCents']")" = "14473" ] || fail "dépenses des charges absentes du budget : $(head -c 400 "$WORK/body")"
STEP=$((STEP + 1))
code=$(curl -s -o "$WORK/body" -w '%{http_code}' -H "Authorization: Bearer $ALICE" -F "file=@$WORK/facture.pdf;type=application/pdf" \
  "$API/api/households/$HOUSEHOLD/charges/$CHARGE/occurrences/$OCCURRENCE/invoices")
[ "$code" = 201 ] || fail "étape $STEP : envoi de la facture → $code : $(head -c 300 "$WORK/body")"
echo "ok $STEP : facture d'échéance → 201"

echo "== Rappels"
eventually 10 call "$ALICE" GET "/households/$HOUSEHOLD/reminders" 200

echo "== Gestion du foyer : retrait d'un membre, suppression d'un foyer"
call "$ALICE" DELETE "/households/$HOUSEHOLD/members/$BOB_ID" 204
# Le retrait atteint chaque domaine par la boîte d'envoi : Bob n'y a plus accès.
eventually 10 call "$BOB" GET "/households/$HOUSEHOLD/shopping-lists" 404
call "$ALICE" POST /households 201 '{"name":"Foyer jetable"}'
SPARE=$(body "['id']")
call "$ALICE" DELETE "/households/$SPARE" 400 '{"confirmName":"autre nom"}'
call "$ALICE" DELETE "/households/$SPARE" 204 '{"confirmName":"foyer jetable"}'
call "$ALICE" GET "/households/$SPARE" 404

echo "== Parcours natif réussi ($STEP étapes)"
