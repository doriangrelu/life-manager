## 1. Composants communs

- [x] 1.1 [frontend/ui] `Field` (libellé, icône « i », champ, message d'erreur ; erreur montrée une fois le champ quitté ou le formulaire envoyé) et `InfoTip` (bulle au survol, au clavier et au toucher)
- [x] 1.2 [frontend] Règles de contrôle réutilisables `lib/validation.ts` (+ tests) ; `Segmented` avec icône « i »

## 2. Formulaires d'argent (v0.9.0)

- [x] 2.1 [frontend/budget] Dépense et rentrée : montant, catégorie, date ; « i » sur « Pour » et « Reçu par »
- [x] 2.2 [frontend/charges] Récurrent et crédit : nom, montants, assurance, dates, catégorie ; « i » sur « Fixe / Variable », l'assurance et la dernière mensualité
- [x] 2.3 [frontend/budget + charges] Budget prévu et confirmation d'un montant ; « i » sur « Tous les mois / Ce mois seulement »
- [x] 2.4 [frontend] Vérification au navigateur
- [x] 2.5 [infra/vps] Version v0.9.0 publiée et déployée

## 3. Autres formulaires (v0.9.1)

- [x] 3.1 [frontend/vehicle] Véhicule et opération d'entretien (nom, kilométrage, coût, dates) ; « i » sur les prochaines échéances
- [x] 3.2 [frontend] Création d'un foyer, code d'invitation (8 caractères), renommages, suppression du foyer (nom qui ne correspond pas). Les ajouts en une ligne (catégorie, sous-liste, article) ne changent pas : leur bouton reste inactif tant que le champ est vide
- [x] 3.3 [frontend/household] « i » sur l'option « Rentrées et bilan »
- [x] 3.4 [frontend] Vérification au navigateur
- [x] 3.5 [infra/vps] Version v0.9.1 publiée et déployée
