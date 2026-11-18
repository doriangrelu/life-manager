## 1. Sous-listes

- [x] 1.1 [shopping/domain] Value objects `ShoppingListId`, `ListName` et agrégat `ShoppingList` (création, renommage, sous-listes par défaut) ; vérifié par des tests unitaires
- [x] 1.2 [shopping/infrastructure/out] Migration `V2` (tables `shopping_list`, `purchase_history`, colonne `shopping_item.list_id`, reprise des données vers « Courses ») ; vérifié par un test d'intégration qui applique `V1`, insère un article, applique `V2` et retrouve l'article dans « Courses »
- [x] 1.3 [shopping/infrastructure/out] Repository, mapper et adaptateur des sous-listes ; `ShoppingItem` rattaché à une sous-liste ; vérifié par les tests de 1.5
- [x] 1.4 [shopping/application] `ShoppingListAccess`, `CreateDefaultShoppingLists` (écouteur `HouseholdCreatedV1`), `ListShoppingLists` avec compteurs, `AddShoppingList`, `RenameShoppingList`, `DeleteShoppingList` ; vérifié par des tests d'intégration (3 sous-listes par défaut, 409 sur doublon, suppression en cascade, 404 hors foyer)
- [x] 1.5 [shopping/infrastructure/in/web] Routes `/shopping-lists` et `/shopping-lists/{listId}/items…` ; anciennes routes retirées ; vérifié par la reprise des tests d'intégration existants des articles sur les nouvelles routes et un test « sous-listes séparées »

## 2. Historique des achats

- [ ] 2.1 [shopping/domain] Agrégat `PurchaseHistoryEntry` (`recordPurchase`) ; vérifié par des tests unitaires
- [ ] 2.2 [shopping/application + infrastructure] `ClearCheckedItems` alimente l'historique (libellé sans tenir compte des majuscules, compteur, date) ; vérifié par des tests d'intégration reprenant les scénarios « Premier achat » et « Article racheté »
- [ ] 2.3 [shopping] `ListPurchaseHistory`, `RestockFromHistory` (tout ou rien), `ForgetHistoryEntry` et leurs routes ; vérifié par des tests d'intégration (remise de 3 articles, 404 sans ajout partiel, oubli)

## 3. PWA

- [ ] 3.1 [frontend] Client d'API des sous-listes et de l'historique ; vérifié par `bun run check`
- [ ] 3.2 [frontend] Pastilles de sous-listes avec compteurs, sous-liste mémorisée sur l'appareil, gestion (ajout, renommage, suppression avec confirmation) ; vérifié dans le navigateur
- [ ] 3.3 [frontend] Écran d'historique avec sélection multiple et remise en liste ; vérifié dans le navigateur, desktop et mobile

## Workflow follow-up

- À l'archivage, mettre à jour le `## Purpose` de `openspec/specs/liste-courses` (il parle encore d'une liste unique).
- Archiver le change une fois relu et implémenté.
