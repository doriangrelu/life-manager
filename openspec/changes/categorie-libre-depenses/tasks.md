## 1. Catégories : « Autre » en dernier

- [x] 1.1 [budget/infrastructure/out] Tri `lower(name) = 'autre', lower(name)` dans la liste des catégories ; vérifié par un test d'intégration (« Autre » en dernier après les catégories par défaut et une catégorie ajoutée)

## 2. Nouvelle catégorie à la saisie

- [x] 2.1 [budget/application/port] `ExpenseInput` et `ExpenseRequest` : `categoryId` facultatif, `newCategoryName` facultatif ; `CategoryRepository.findByName` (sans tenir compte des majuscules) ; vérifié par la compilation et les tests existants verts
- [x] 2.2 [budget/application/service] `ExpenseCategoryResolver` (catégorie existante, nom existant réutilisé et réactivé si archivé, création, refus si les deux ou aucun) branché dans `ExpenseRules` ; vérifié par des tests d'intégration couvrant chaque scénario de la spec, dont « montant invalide → aucune catégorie créée »
- [x] 2.3 [budget/application/service] Création simultanée du même nom : insertion `ON CONFLICT DO NOTHING` puis relecture de la catégorie existante ; vérifié par un test qui enregistre deux dépenses en parallèle avec le même nouveau nom et obtient une seule catégorie

## 3. PWA

- [x] 3.1 [frontend] Champ « Préciser (facultatif) » sous la puce « Autre », envoi de `newCategoryName`, rechargement des catégories ; vérifié par `bun run check` et dans le navigateur (dépense « Cadeaux » puis catégorie proposée à la saisie suivante)
- [x] 3.2 [frontend] « Autre » en dernier dans les puces et dans la gestion des catégories ; vérifié dans le navigateur

## Workflow follow-up

- Archiver le change une fois relu et implémenté.
