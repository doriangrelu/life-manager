> Lot lancé par Dorian le 2026-11-19 (« tu peux te lancer ») ; navigation par espaces déléguée (« à toi de voir »).

## 1. PWA : socle

- [ ] 1.1 [frontend/ui] Atomic Design : `lib/ui` rangé en atoms, molecules, organisms ; imports mis à jour
- [ ] 1.2 [frontend/lib] `personName`, `lib/members.ts`, `scrollToTop` (+ tests) ; noms mis en forme à la sortie de l'API

## 2. PWA : retouches

- [ ] 2.1 [frontend/budget] Bouton « Aujourd'hui » ; saisie enchaînée qui repart en haut
- [ ] 2.2 [frontend] Retour en haut à chaque changement d'écran

## 3. Navigation par espaces

- [ ] 3.1 [frontend/lib] `lib/navigation.ts` : espaces, sections, dernier écran mémorisé (+ tests)
- [ ] 3.2 [frontend/ui] Barre du bas à 4 espaces, pastilles d'espace, menu latéral groupé

## 4. Ordre des courses

- [ ] 4.1 [shopping/domain + infrastructure] `position` sur articles et sous-listes, migration V4, tri (+ tests)
- [ ] 4.2 [shopping/application + infrastructure/in] Use cases et API de réordonnancement (+ tests d'intégration)
- [ ] 4.3 [frontend/shopping] Glisser-déposer des articles et des sous-listes (`svelte-dnd-action`)

## 5. Livraison

- [ ] 5.1 [frontend] Vérification dans le navigateur : mobile et bureau, clair et sombre
- [ ] 5.2 [doc] Règle Atomic Design et navigation dans `docs/architecture.md`
- [ ] 5.3 [ci + infra/vps] CI verte, version publiée et déployée
