## 1. Composants communs

- [x] 1.1 [frontend/ui] `Field` (libellé, icône « i », champ, message d'erreur ; erreur montrée une fois le champ quitté ou le formulaire envoyé) et `InfoTip` (bulle au survol, au clavier et au toucher)
- [x] 1.2 [frontend] Règles de contrôle réutilisables `lib/validation.ts` (+ tests) ; `Segmented` avec icône « i »

## 2. Formulaires d'argent (v0.9.0)

- [x] 2.1 [frontend/budget] Dépense et rentrée : montant, catégorie, date ; « i » sur « Pour » et « Reçu par »
- [x] 2.2 [frontend/charges] Récurrent et crédit : nom, montants, assurance, dates, catégorie ; « i » sur « Fixe / Variable », l'assurance et la dernière mensualité
- [x] 2.3 [frontend/budget + charges] Budget prévu et confirmation d'un montant ; « i » sur « Tous les mois / Ce mois seulement »
- [x] 2.4 [frontend] Vérification au navigateur
- [ ] 2.5 [infra/vps] Version v0.9.0 publiée et déployée

## 3. Autres formulaires (version suivante)

- [ ] 3.1 [frontend/vehicle] Véhicule et opération d'entretien (kilométrage, coût, dates) ; « i » sur la prochaine échéance
- [ ] 3.2 [frontend] Catégories, listes de courses, création d'un foyer, code d'invitation, renommages
- [ ] 3.3 [frontend/household] « i » sur l'option « Rentrées et bilan »
