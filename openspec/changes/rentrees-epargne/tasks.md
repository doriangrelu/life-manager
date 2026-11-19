> Modèle convergé avec Dorian le 2026-11-19. **Attendre son « go » explicite avant d'implémenter.**

## 1. Réglage du foyer (`household`)

- [ ] 1.1 [household/domain + infrastructure] `incomeTracking` dans `Household` (propriétaire), migration (+ tests)
- [ ] 1.2 [household/application + infrastructure/in] Route `PUT /settings`, réglage dans la consultation (+ tests d'intégration 403)

## 2. Rentrées (`budget`)

- [ ] 2.1 [budget/domain] `CategoryKind`, agrégat `Income`, invariants (+ tests)
- [ ] 2.2 [budget/infrastructure] Migration (`category.kind`, table `income`, lien d'échéance), dépôts (+ tests)
- [ ] 2.3 [budget/application + infrastructure/in] Rentrées et catégories de rentrées (suggestions au premier usage), cloisonnement (+ tests d'intégration)
- [ ] 2.4 [budget/application] Vue du mois (rentré, résultat) et bilan par période (+ tests)

## 3. Récurrents (`bills`)

- [ ] 3.1 [bills/domain + infrastructure] `FlowDirection`, migration `direction` (+ tests)
- [ ] 3.2 [bills/contract + budget/infrastructure/in/event] `ChargePaidV1` avec le sens → dépense ou rentrée, sans doublon (+ tests)
- [ ] 3.3 [bills/application + infrastructure/in] Prévisions de la période (`/charges/forecast`) (+ tests)

## 4. PWA

- [ ] 4.1 [frontend/api + lib] API (réglage, rentrées, catégories, bilan, prévisions) ; calcul du prévu (+ tests)
- [ ] 4.2 [frontend/household] Interrupteur « Suivre les rentrées » (propriétaire)
- [ ] 4.3 [frontend/budget] + Dépense / Rentrée, bandeau à trois chiffres, section rentrées (mode activé seulement)
- [ ] 4.4 [frontend/charges] Panneaux Dépenses fixes / Rentrées fixes
- [ ] 4.5 [frontend/bilan] Sous-onglet Bilan : périodes, réalisé / prévu, épargne, détails
- [ ] 4.6 [frontend] Vérification : mode désactivé identique à aujourd'hui ; mode activé sur mobile et bureau, clair et sombre

## 5. Livraison

- [ ] 5.1 [ci] Parcours natif étendu (réglage, rentrée, rentrée récurrente, bilan)
- [ ] 5.2 [doc] Architecture et feuille de route
- [ ] 5.3 [infra/vps] Version publiée et déployée
