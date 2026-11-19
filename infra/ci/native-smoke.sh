#!/usr/bin/env bash
# Parcours de vérification de l'exécutable natif : un vrai scénario d'usage sur l'API compilée par GraalVM.
# Attrape ce que les tests JVM ne voient pas (réflexion, ressources, classes chargées dynamiquement…).
#
# Prérequis : PostgreSQL et Keycloak de développement lancés (docker compose up -d postgres keycloak),
# comptes de passage créés et supprimés par le script, API native joignable sur $API.
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
token() {
  curl -sSf -d grant_type=password -d client_id=life-manager-pwa -d scope=openid -d "username=$1" -d password=dev-password \
    "$KEYCLOAK/realms/life-manager/protocol/openid-connect/token" | json "['access_token']"
}
# Comptes neufs à chaque passage : le parcours ne dépend pas de l'état des comptes de dev (limite de 10 foyers…).
new_user() { # email prénom
  kcadm create users -r life-manager -s "username=$1" -s "email=$1" -s emailVerified=true -s enabled=true \
    -s "firstName=$2" -s lastName=Smoke >/dev/null
  kcadm set-password -r life-manager --username "$1" --new-password dev-password
}
# Ménage final : comptes de passage supprimés (s'ils existent encore), mot de passe direct refermé.
forget_user() {
  local id
  id=$(kcadm get users -r life-manager -q "email=$1" --fields id --format csv --noquotes || true)
  [ -z "$id" ] || kcadm delete "users/$id" -r life-manager || true
}
STAMP="$(date +%s)-$RANDOM"
ALICE_EMAIL="smoke-alice-$STAMP@example.org"
BOB_EMAIL="smoke-bob-$STAMP@example.org"
trap 'forget_user "$ALICE_EMAIL"; forget_user "$BOB_EMAIL"; kcadm update "clients/$PWA_CLIENT" -r life-manager -s directAccessGrantsEnabled=false; rm -rf "$WORK"' EXIT
new_user "$ALICE_EMAIL" Alice
new_user "$BOB_EMAIL" Bob
ALICE=$(token "$ALICE_EMAIL")
BOB=$(token "$BOB_EMAIL")

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
# Budget prévu : fixé pour tous les mois, relu dans la vue du mois.
call "$ALICE" PUT "/households/$HOUSEHOLD/expense-categories/$CATEGORY/budget" 204 \
  "{\"month\":\"$MONTH\",\"scope\":\"FROM_MONTH\",\"plannedCents\":40000}"
call "$ALICE" GET "/households/$HOUSEHOLD/budget/$MONTH" 200
[ "$(body "['plans'][0]['plannedCents']")" = "40000" ] || fail "budget prévu absent : $(head -c 400 "$WORK/body")"

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

echo "== Rentrées et bilan : réglage du foyer, rentrée saisie, rentrée récurrente, prévisions"
call "$ALICE" PUT "/households/$HOUSEHOLD/settings" 204 '{"incomeTracking":true}'
call "$BOB" PUT "/households/$HOUSEHOLD/settings" 403 '{"incomeTracking":false}'
call "$ALICE" GET "/households/$HOUSEHOLD/income-categories" 200
SALARY=$(python3 -c "import json,sys; print(next(c['id'] for c in json.load(sys.stdin) if c['name']=='salaire'))" < "$WORK/body")
call "$ALICE" POST "/households/$HOUSEHOLD/incomes" 201 \
  "{\"amountCents\":100000,\"categoryId\":\"$SALARY\",\"newCategoryName\":null,\"receivedOn\":\"$TODAY\",\"receivedBy\":\"$BOB_ID\",\"label\":null}"
call "$ALICE" POST "/households/$HOUSEHOLD/charges" 201 \
  "{\"name\":\"Salaire\",\"amountCents\":200000,\"amountKind\":\"FIXED\",\"frequency\":\"MONTHLY\",\"firstDueOn\":\"$TODAY\",\"budgetCategoryId\":\"$SALARY\",\"payerId\":\"$ALICE_ID\",\"beneficiaryId\":null,\"direction\":\"INCOME\"}"
# 1 000 € saisis + 2 000 € de la rentrée récurrente du jour ; résultat = 3 000 € − 144,73 €.
eventually 10 call "$ALICE" GET "/households/$HOUSEHOLD/budget/$MONTH" 200
[ "$(body "['incomeCents']")" = "300000" ] || fail "rentrées du mois inattendues : $(head -c 400 "$WORK/body")"
[ "$(body "['resultCents']")" = "285527" ] || fail "résultat du mois inattendu : $(head -c 400 "$WORK/body")"
call "$ALICE" GET "/households/$HOUSEHOLD/report?from=$MONTH&to=$MONTH" 200
[ "$(body "['resultCents']")" = "285527" ] || fail "bilan inattendu : $(head -c 400 "$WORK/body")"
call "$ALICE" GET "/households/$HOUSEHOLD/charges/forecast?from=$TODAY&to=$(date -d '+2 months' +%F)" 200
[ "$(body "[0]['status']")" != "" ] || fail "prévisions vides : $(head -c 400 "$WORK/body")"
# Crédit : prêt + assurance en une mensualité, avec une dernière échéance (première dans 40 jours : rien n'est payé ici).
call "$ALICE" POST "/households/$HOUSEHOLD/charges" 201 \
  "{\"name\":\"Pret\",\"amountCents\":61240,\"amountKind\":\"FIXED\",\"frequency\":\"MONTHLY\",\"firstDueOn\":\"$(date -d '+40 days' +%F)\",\"budgetCategoryId\":\"$CATEGORY\",\"payerId\":\"$ALICE_ID\",\"beneficiaryId\":null,\"lastDueOn\":\"$(date -d '+400 days' +%F)\",\"loan\":{\"insuranceCents\":2400}}"
[ "$(body "['loan']['insuranceCents']")" = "2400" ] || fail "crédit mal enregistré : $(head -c 400 "$WORK/body")"

echo "== Rappels"
eventually 10 call "$ALICE" GET "/households/$HOUSEHOLD/reminders" 200
# Rappel saisi, « c'est fait », puis la ligne d'historique (lisible, retirable).
call "$ALICE" POST "/households/$HOUSEHOLD/personal-reminders" 201 \
  "{\"title\":\"Ramonage\",\"note\":null,\"dueOn\":\"$TODAY\",\"recurrence\":\"YEARLY\",\"notice\":\"ONE_WEEK\"}"
REMINDER=$(body "['id']")
call "$ALICE" POST "/households/$HOUSEHOLD/personal-reminders/$REMINDER/done" 204
call "$ALICE" GET "/households/$HOUSEHOLD/reminder-history" 200
[ "$(body "[0]['title']")" = "Ramonage" ] || fail "historique des rappels inattendu : $(head -c 400 "$WORK/body")"
call "$ALICE" DELETE "/households/$HOUSEHOLD/reminder-history/$(body "[0]['id']")" 204

echo "== Version (publique, sans jeton)"
curl -sSf -m 20 "$API/api/version" | grep -q '"version"' || fail "version de l'API absente"

echo "== Mon compte : CGU, export, suppression (compte jetable, vrai Keycloak)"
call "$ALICE" GET /whoami 200
TERMS=$(body "['termsVersion']")
call "$ALICE" PUT /me/terms 400 '{"version":"2000-01-01"}'
call "$ALICE" PUT /me/terms 204 "{\"version\":\"$TERMS\"}"
call "$ALICE" GET /me/export 200
[ "$(body "['profil']['email']")" = "$ALICE_EMAIL" ] || fail "export inattendu : $(head -c 400 "$WORK/body")"
SPARE_EMAIL="jetable-$RANDOM-$(date +%s)@example.org"
kcadm create users -r life-manager -s "username=$SPARE_EMAIL" -s "email=$SPARE_EMAIL" -s emailVerified=true \
  -s enabled=true -s firstName=Jetable -s lastName=Test >/dev/null
kcadm set-password -r life-manager --username "$SPARE_EMAIL" --new-password dev-password
SPARE_TOKEN=$(token "$SPARE_EMAIL")
call "$SPARE_TOKEN" GET /whoami 200
call "$SPARE_TOKEN" POST /households 201 '{"name":"Foyer jetable"}'
call "$SPARE_TOKEN" DELETE /me 400 '{"confirmEmail":"autre@example.org"}'
call "$SPARE_TOKEN" DELETE /me 204 "{\"confirmEmail\":\"$SPARE_EMAIL\"}"
[ "$(kcadm get users -r life-manager -q "email=$SPARE_EMAIL" --fields id --format csv --noquotes | wc -l)" = "0" ] \
  || fail "compte Keycloak jetable non supprimé"

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
