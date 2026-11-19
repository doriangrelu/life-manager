## 1. Backend : budget prévu

- [ ] 1.1 [backend/budget] Agrégat `CategoryBudget` et value objects (mois, portée `FROM_MONTH` / `MONTH_ONLY`, montant facultatif) + règle de résolution du prévu d'un mois (+ tests domaine)
- [ ] 1.2 [backend/budget] Migration `budget/V5` (table `category_budget`), dépôt, effacement avec le foyer
- [ ] 1.3 [backend/budget] Use case « régler le prévu d'une catégorie » et route `PUT /categories/{c}/budget` (+ tests d'intégration : 204, 400, 404, cloisonnement)
- [ ] 1.4 [backend/budget] Vue du mois : `plans` et `recurringExpenseCents` ; bilan : `recurringExpenseCents` (+ tests)

## 2. PWA

- [ ] 2.1 [frontend/budget] Calculs (`lib/plan.ts`) : avancement par catégorie avec les échéances à venir, seuils 80 % et dépassement, reste à vivre (+ tests)
- [ ] 2.2 [frontend/budget] « Par catégorie » : dépensé / prévu, barre en trois couleurs, part à venir, pastille ; catégories prévues sans dépense
- [ ] 2.3 [frontend/budget] Fenêtre « Budget prévu » (montant, Tous les mois / Ce mois seulement, Retirer)
- [ ] 2.4 [frontend/budget + report] Reste à vivre dans le bandeau du mois et dans le Bilan

## 3. Vérification et livraison

- [ ] 3.1 [frontend] Vérification au navigateur : sans prévu (rien ne change), avec prévu, mois passé, option « Rentrées et bilan » active ou non ; mobile et bureau, clair et sombre
- [ ] 3.2 [infra/ci] Parcours natif étendu (régler un prévu, le relire dans la vue du mois)
- [ ] 3.3 [doc] Feuille de route à jour
- [ ] 3.4 [infra/vps] Version v0.7.0 publiée et déployée
