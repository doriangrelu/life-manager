## 1. Fondations

- [ ] 1.1 [frontend] Jetons de thème clair et sombre dans `app.css`, police embarquée, `lucide-svelte` ; test Vitest du contraste des couples de couleurs texte / fond ; vérifié par `bun run test` et `bun run check`
- [ ] 1.2 [frontend/ui] `Modal` (sur `<dialog>`, panneau du bas sur mobile, centré sur grand écran, focus rendu à l'ouvreur) et `ConfirmDialog` ; vérifié dans le navigateur au clavier (Tab, Échap)
- [ ] 1.3 [frontend/ui] `Chip`, `Segmented`, `Card`, `Fab`, `AmountInput`, `ProgressBar` ; vérifié par `bun run check`
- [ ] 1.4 [frontend] Coque de l'application : en-tête (foyer, profil), `BottomNav` sous 900 px et `SideNav` au-delà, routage des quatre sections ; vérifié dans le navigateur à 390 et 1280 px

## 2. Écrans

- [ ] 2.1 [frontend/budget] Écran du mois selon la maquette (carte du total, barres, dernières dépenses, bouton flottant) ; formulaire de dépense et gestion des catégories en modales ; suppression confirmée par `ConfirmDialog` ; vérifié dans le navigateur
- [ ] 2.2 [frontend/shopping] Écran Courses selon la maquette (saisie, à acheter, dans le panier) ; vérifié dans le navigateur
- [ ] 2.3 [frontend/vehicle] Liste et fiche véhicule selon la maquette (échéances colorées, carnet) ; formulaires véhicule et opération en modales ; vérifié dans le navigateur
- [ ] 2.4 [frontend] Foyer, création ou adhésion, bandeau de rappels dans le nouveau style ; vérifié dans le navigateur

## 3. Vérification d'ensemble

- [ ] 3.1 [frontend] Parcours complet à deux comptes, à 360, 390 et 1280 px, en clair et en sombre : aucun débordement horizontal, cibles de 44 px, aucune boîte `confirm()` ou `prompt()` restante ; `bun run check`, `bun run test`, `bun run build` verts

## Workflow follow-up

- Archiver le change une fois relu et implémenté.
