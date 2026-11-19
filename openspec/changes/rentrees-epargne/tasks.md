> Proposition à relire par Dorian (voir « Open Questions » du design) avant toute implémentation.

## 1. Domaine `budget`

- [ ] 1.1 [budget/domain] Agrégats `Income` et `Saving`, value objects, invariants (+ tests)
- [ ] 1.2 [budget/infrastructure] Migration (tables `income`, `saving`, lien d'échéance élargi), dépôts (+ tests)
- [ ] 1.3 [budget/application + infrastructure/in] Use cases et API : rentrées et épargne (ajouter, modifier, supprimer), cloisonnement (+ tests d'intégration)
- [ ] 1.4 [budget/application] Vue du mois : rentrées, épargne, solde, cumul d'épargne (+ tests)

## 2. Récurrents (`bills`)

- [ ] 2.1 [bills/domain + infrastructure] Sens d'un mouvement récurrent (`FlowDirection`), migration `direction` (+ tests)
- [ ] 2.2 [bills/contract] `ChargePaidV1` avec le sens (lecture tolérante)
- [ ] 2.3 [budget/infrastructure/in/event + application] Échéance payée → dépense, rentrée ou épargne, sans doublon (+ tests)

## 3. PWA

- [ ] 3.1 [frontend/api + lib] API rentrées, épargne, sens des récurrents ; calculs d'affichage (+ tests)
- [ ] 3.2 [frontend/budget] Bandeau du mois à quatre chiffres, sections rentrées et épargne, bouton + à trois choix
- [ ] 3.3 [frontend/charges] Écran « Récurrents » : filtre par sens, formulaire selon le sens
- [ ] 3.4 [frontend] Vérification dans le navigateur : mobile et bureau, clair et sombre

## 4. Livraison

- [ ] 4.1 [ci] Parcours natif étendu (rentrée, épargne, salaire récurrent)
- [ ] 4.2 [doc] Architecture et feuille de route (lot 4 découpé en 4a / 4b)
- [ ] 4.3 [infra/vps] Version publiée et déployée
