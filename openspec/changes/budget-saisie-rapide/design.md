## Context

Le socle est en place (voir `openspec/specs/comptes-utilisateurs` et `openspec/specs/foyer`) : monolithe modulaire Micronaut, un package et un schéma par domaine, domaines reliés uniquement par des événements de contrat (`docs/architecture.md`). Aujourd'hui seul `identity` publie des événements de contrat ; `household` n'en publie aucun. La PWA affiche un foyer sélectionné et connaît déjà les noms de ses membres.

## Goals / Non-Goals

**Goals:**
- Un domaine `budget` autonome : il vérifie seul qui est membre d'un foyer, sans appeler `household`.
- Des montants exacts (aucun calcul en virgule flottante).
- Une vue mensuelle calculée par la base, pas en mémoire.

**Non-Goals:**
- Reprise des foyers existants (aucune donnée de production à ce stade).
- Outbox et broker externe (inchangé depuis le socle).

## Decisions

### 1. Domaine `budget`, schéma `budget`

Nouveau package de premier niveau `budget`, nouvelle datasource `budget` (compte PostgreSQL `budget_app`), migrations Flyway dans `db/migration/budget`. Aucune clé étrangère vers `household` ou `identity`.

### 2. Agrégats

**`Category`** — invariants : nom valide, appartient à un seul foyer.
- Value objects : `CategoryId`, `CategoryName` (1 à 40 caractères), `HouseholdId` (propre au budget).
- Méthodes : `create`, `rename`, `archive`, `reactivate`.
- L'unicité du nom dans un foyer porte sur plusieurs catégories : vérifiée par le use case et garantie par un index unique `(household_id, lower(name))`.

**`Expense`** — invariants : montant strictement positif et plafonné, date pas dans le futur, libellé court.
- Value objects : `ExpenseId`, `Money` (centimes en `long`, euros uniquement), `ExpenseDate` (`LocalDate`), `ExpenseLabel` (facultatif, 140 caractères), `MemberId`, `Beneficiary` (interface scellée : `Shared` ou `Member(MemberId)`).
- Méthodes : `record`, `correct`, et suppression par le repository après `markDeleted()` (pour produire l'événement).
- Événements de domaine : `ExpenseRecorded`, `ExpenseCorrected`, `ExpenseDeleted` (sans consommateur pour l'instant, prêts pour les futurs rappels ou équilibres).

Deux agrégats séparés : une dépense ne protège aucune règle de la catégorie et inversement. Les règles qui les relient (catégorie active et du même foyer) sont vérifiées par un service applicatif.

### 3. Connaître les membres sans dépendre de `household`

`household` publie deux nouveaux événements de contrat, après commit, via un relais identique à celui d'`identity` :
- `HouseholdCreatedV1(householdId, founderId, occurredAt)`
- `MemberJoinedV1(householdId, memberId, occurredAt)`

`budget` les écoute (`infrastructure/in/event`) et tient une copie locale `budget.household_member(household_id, member_id)`. Sur `HouseholdCreatedV1`, il crée aussi les catégories par défaut.
Alternative écartée : interroger `household` par un port synchrone — plus simple aujourd'hui, mais deviendrait un appel réseau en cas de découpage.

### 4. Services applicatifs

- `BudgetAccess` : vérifie qu'un utilisateur est membre du foyer (copie locale), sinon `404` — utilisé par tous les use cases.
- `ExpenseRules` : vérifie que la catégorie est active et du même foyer, et que payeur et bénéficiaire sont membres — partagé par `RecordExpense` et `CorrectExpense`.

### 5. Use cases

Catégories : `ListCategories`, `AddCategory`, `RenameCategory`, `ArchiveCategory`, `ReactivateCategory`, `CreateDefaultCategories` (appelé par l'écouteur).
Dépenses : `RecordExpense`, `CorrectExpense`, `DeleteExpense`.
Vue : `GetMonthlyBudget`, adossé à un port de lecture `MonthlyBudgetReadModel` (requêtes SQL d'agrégation `GROUP BY`) : une lecture n'a pas besoin de charger des agrégats.
Membres : `RegisterHouseholdMember` (appelé par l'écouteur).

### 6. Base de données (schéma `budget`)

- `household_member(household_id, member_id, PK (household_id, member_id))`
- `category(id, household_id, name, archived)` + index unique `(household_id, lower(name))`
- `expense(id, household_id, category_id FK, amount_cents CHECK > 0, spent_on DATE, paid_by, beneficiary NULL = commun, label, created_by, created_at)` + index `(household_id, spent_on)`

### 7. API (`/api/households/{householdId}/…`)

| Route | Use case |
|---|---|
| `GET` / `POST /expense-categories` | lister / ajouter |
| `PUT /expense-categories/{id}` | renommer |
| `POST /expense-categories/{id}/archive` et `/reactivate` | archiver / réactiver |
| `POST /expenses` | saisir |
| `PUT /expenses/{id}` / `DELETE /expenses/{id}` | modifier / supprimer |
| `GET /budget/{yyyy-MM}` | vue mensuelle |

Montants échangés en centimes entiers (`amountCents`) ; bénéficiaire `null` = commun. Les noms des membres ne sont pas renvoyés : la PWA les a déjà (détail du foyer).

### 8. PWA

Deux onglets dans le foyer sélectionné : « Budget » (par défaut) et « Foyer » (écran existant). L'onglet Budget montre le mois avec un bouton de saisie qui ouvre le formulaire rapide (`inputmode="decimal"`, catégories en boutons, valeurs pré-remplies). La conversion « 12,50 » → 1250 centimes se fait côté PWA, et est testée.

## Risks / Trade-offs

- **Copie des membres désynchronisée** (événement perdu sans outbox) → un membre ne pourrait pas utiliser le budget de son foyer ; risque accepté à cette échelle, outbox à prévoir avec le broker.
- **Foyers créés avant ce change** → ni membres ni catégories côté budget ; en dev, réinitialiser la base (`docker compose down -v`).
- **Suppression définitive** → pas de corbeille ; acceptable pour une saisie personnelle, la modification couvre la plupart des erreurs.

## Migration Plan

Nouveau schéma et nouveau compte PostgreSQL ajoutés au script d'initialisation de dev ; migration Flyway `V1` du schéma `budget`. Aucune donnée existante à reprendre.
