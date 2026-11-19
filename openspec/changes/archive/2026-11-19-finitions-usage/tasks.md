> Lot lancé par Dorian le 2026-11-19 (« tu peux te lancer ») ; navigation par espaces déléguée (« à toi de voir »).

## 1. PWA : socle

- [x] 1.1 [frontend/ui] Atomic Design : `lib/ui` rangé en atoms, molecules, organisms ; imports mis à jour
- [x] 1.2 [frontend/lib] `personName`, `lib/members.ts`, `scrollToTop` (+ tests) ; noms mis en forme à la sortie de l'API

## 2. PWA : retouches

- [x] 2.1 [frontend/budget] Bouton « Aujourd'hui » ; saisie enchaînée qui repart en haut
- [x] 2.2 [frontend] Retour en haut à chaque changement d'écran

## 3. Navigation par espaces

- [x] 3.1 [frontend/lib] `lib/navigation.ts` : espaces, sections, dernier écran mémorisé (+ tests)
- [x] 3.2 [frontend/ui] Barre du bas à 4 espaces, pastilles d'espace, menu latéral groupé

## 4. Ordre des courses

- [x] 4.1 [shopping/infrastructure] `position` sur articles et sous-listes, gérée par le stockage (pas un invariant d'agrégat), migration V4, tri (+ tests)
- [x] 4.2 [shopping/application + infrastructure/in] Use cases et API de réordonnancement (+ tests d'intégration)
- [x] 4.3 [frontend/shopping] Glisser-déposer des articles (liste) et des sous-listes (fenêtre de gestion), `svelte-dnd-action`

## 5. Sécurité (demande de Dorian en cours de lot)

- [x] 5.1 [tous domaines] Audit du cloisonnement des foyers ; contrôle d'accès avant l'écriture des fichiers joints
- [x] 5.2 [tests] Règle ArchUnit (tout use case passe par un `…Access`) et balayage de toutes les routes de foyer par un non-membre

## 6. Livraison

- [x] 6.1 [frontend] Vérification dans le navigateur : mobile et bureau, clair et sombre, glisser-déposer au clavier
- [x] 6.2 [doc] Règles PWA (Atomic Design, navigation, glisser-déposer) et cloisonnement dans `docs/architecture.md`
- [x] 6.3 [ci + infra/vps] CI verte, version publiée et déployée (v0.4.0, 2026-11-19)
