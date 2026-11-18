## Context

Domaine `budget` (voir `openspec/specs/depenses` et `openspec/specs/categories-depenses`). Aujourd'hui `RecordExpense` et `CorrectExpense` reçoivent un `ExpenseInput` avec un `categoryId` obligatoire ; `ExpenseRules` vérifie que la catégorie est active et du même foyer. Les catégories sont triées par `lower(name)` dans `CategoryJdbcRepository`.

## Goals / Non-Goals

**Goals:**
- Créer ou réutiliser une catégorie dans la même transaction que la dépense.
- Garder les agrégats `Category` et `Expense` inchangés dans leurs invariants.

**Non-Goals:**
- Marquer « Autre » d'un statut technique particulier (voir décision 3).

## Decisions

### 1. Choix de la catégorie : un service applicatif dédié

Nouveau service `ExpenseCategoryResolver` (dans `budget/application/service`), utilisé par `ExpenseRules` :
- `categoryId` fourni → comportement actuel (catégorie active du foyer, sinon `400`).
- `newCategoryName` fourni → recherche par nom sans tenir compte des majuscules (`CategoryRepository.findByName`) ; trouvée : réactivée si archivée puis sauvegardée ; absente : `Category.create`, sauvegardée.
- Les deux ou aucun → `InvalidValueException` (`400`).

La règle « un nom unique par foyer » reste garantie par l'index unique `(household_id, lower(name))`. La création passe par `INSERT … ON CONFLICT (household_id, (lower(name))) DO NOTHING` : si deux membres créent le même nom en même temps, le second n'insère rien, sans erreur, et relit la catégorie du premier. (Une violation de contrainte rendrait la transaction PostgreSQL inutilisable : impossible de relire ensuite.)

Pourquoi un service plutôt que de laisser le contrôleur appeler `AddCategory` puis `RecordExpense` : une seule transaction, donc ni catégorie orpheline si la dépense est refusée, ni appel en deux temps côté PWA.

### 2. Contrat d'API

`ExpenseRequest.categoryId` devient facultatif et `newCategoryName` est ajouté (`@Size(max = 40)`, facultatif). La règle « exactement l'un des deux » est vérifiée côté application (message clair en `400`). `ExpenseInput` reçoit le champ correspondant.

### 3. « Autre » en dernier

Tri dans la requête SQL : `ORDER BY lower(name) = 'autre', lower(name)`. La catégorie est reconnue par son nom : simple, et cohérent avec ce que voit l'utilisateur. Si un foyer renomme « Autre », elle reprend sa place alphabétique, ce qui est le comportement attendu pour une catégorie devenue ordinaire.
Alternative écartée : un drapeau `catch_all` en base, qui demande une migration et une règle de plus (que se passe-t-il si on l'archive ?) sans bénéfice visible.

### 4. PWA

Dans `ExpenseForm`, la puce « Autre » affiche un champ « Préciser (facultatif) ». À l'envoi : précision non vide → `newCategoryName`, sinon `categoryId` de « Autre ». Après une saisie avec précision, la liste des catégories est rechargée pour proposer la nouvelle.

## Risks / Trade-offs

- [Faute de frappe qui crée une catégorie en trop] → la gestion des catégories permet de l'archiver ou de la renommer ; une fusion de catégories reste hors périmètre.
- [Tri reposant sur le nom « Autre »] → documenté ; aucun impact sur les données.

## Migration Plan

Aucune migration de base : seul le tri et le contrat d'API évoluent, de façon rétrocompatible (`categoryId` reste accepté seul).
