## 1. Backend

- [x] 1.1 [backend/bills] `ChargeSchedule.lastDueOn` (dates et prévisions bornées), value object `Loan` dans `ChargeDetails` avec ses invariants (+ tests domaine)
- [x] 1.2 [backend/bills] Migration `bills/V4`, entité, mapper ; `lastDueOn` et `loan` dans la saisie, la vue et la réponse
- [x] 1.3 [backend/bills] Fin automatique : le planificateur termine la charge après sa dernière échéance (+ tests d'intégration : dépense du mois, fin, prévisions, saisies refusées)

## 2. PWA

- [x] 2.1 [frontend/charges] Choix Dépense / Crédit à l'ajout ; formulaire du crédit (mensualité du prêt, assurance, dernière mensualité)
- [x] 2.2 [frontend/charges] Ligne et détail d'un crédit : prêt + assurance, mensualités restantes, date de fin, « Terminé » (+ tests des calculs)

## 3. Vérification et livraison

- [x] 3.1 [frontend] Vérification au navigateur : mobile et bureau, clair et sombre
- [x] 3.2 [infra/ci] Parcours natif étendu (ajout d'un crédit)
- [x] 3.3 [doc] Feuille de route à jour
- [ ] 3.4 [infra/vps] Version v0.8.0 publiée et déployée
