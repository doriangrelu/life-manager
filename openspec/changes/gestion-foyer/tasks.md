> Relu par Dorian le 2026-11-19 : réponses dans « Open Questions » du design (invitations réservées au propriétaire, renommage ajouté).

## 1. Domaine `household`

- [x] 1.1 [household/domain] Propriétaire dans `Household` : transmission, retrait, départ, renommage, suppression, invariants (+ tests)
- [x] 1.2 [household/application] Invitations réservées au propriétaire (génération, annulation, code visible du seul propriétaire) (+ tests)
- [x] 1.3 [household/infrastructure] Migration V4 `owner_id` (reprise : plus ancien membre), dépôts, effacement d'un foyer (+ test de migration)
- [x] 1.4 [household/application + infrastructure/in] Use cases et routes : propriétaire, retrait, départ, annulation, suppression ; `ownerId` dans la consultation (+ tests d'intégration, 403 / 404)
- [x] 1.5 [household/contract + infrastructure/out] Événements `MemberLeftV1` et `HouseholdDeletedV1` par la boîte d'envoi

## 2. Autres domaines

- [x] 2.1 [budget, vehicle, shopping, reminder / infrastructure/in/event + application] Départ d'un membre : retrait de la copie locale (+ tests)
- [x] 2.2 [bills/application] Départ d'un membre : charges réattribuées au propriétaire et à « Commun » (+ tests)
- [x] 2.3 [budget, bills, vehicle, shopping, reminder / application + infrastructure] Suppression d'un foyer : effacement des données, fichiers après validation (+ tests)
- [x] 2.4 [tests] Parcours bout en bout : membre retiré → 404 partout ; foyer supprimé → plus aucune ligne ni fichier

## 3. PWA

- [x] 3.1 [frontend/api + lib] API du foyer, droits déduits du propriétaire (+ tests)
- [x] 3.2 [frontend/components] Écran Foyer : badge propriétaire, transmission, retrait, départ, annulation du code, suppression avec saisie du nom
- [x] 3.3 [frontend] Foyer devenu inaccessible : bascule propre ; « Ancien membre » dans le budget et les charges
- [x] 3.4 [frontend] Vérification dans le navigateur : mobile et bureau, clair et sombre

## 4. Livraison

- [x] 4.1 [ci] Parcours natif étendu (retrait d'un membre, suppression d'un foyer)
- [x] 4.2 [doc] Architecture (événements, cloisonnement) et feuille de route
- [ ] 4.3 [infra/vps] Version publiée et déployée
